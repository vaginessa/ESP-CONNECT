package au.com.umranium.espconnect.app.taskscreens.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import au.com.umranium.espconnect.rx.Scheduler;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static au.com.umranium.espconnect.rx.ToInstance.instance;
import static rx.Observable.error;

@RunWith(JMockit.class)
public class NetworkPollingCallTest {

  @Injectable
  private long intervalSeconds = 1;

  @Injectable
  private int intervalCount = 10;

  @Injectable
  private Func1<Throwable, Boolean> errorCall;

  @Injectable
  private Func0<Observable<Integer>> apiCall;

  @Injectable
  private Scheduler scheduler;

  TestScheduler computation = new TestScheduler();

  @Injectable
  private WifiConnectionUtil wifiConnectionUtil;

  @Tested
  NetworkPollingCall<Integer> networkPollingCall;

  TestSubscriber<Integer> subscriber = new TestSubscriber<>();

  @Before
  public void setUp() throws Exception {
    new Expectations() {{
      scheduler.mainThread();
      returns(Schedulers.immediate());
      minTimes = 0;

      scheduler.io();
      returns(Schedulers.immediate());
      minTimes = 0;

      scheduler.computation();
      returns(computation);
      minTimes = 0;
    }};
  }

  @Test
  public void ifConnected_makesCall() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(true);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);

    // then:
    new Verifications() {{
      apiCall.call();
      times = 1;
    }};
  }

  @Test
  public void ifNotConnectedInitially_retriesAfterInterval() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true, true, true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(false, false, true);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);
    computation.advanceTimeBy(2, TimeUnit.SECONDS);

    // then:
    new Verifications() {{
      apiCall.call();
    }};
  }

  @Test
  public void ifNotConnectedTillTimeout_throwsTimeoutException() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(false);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);
    computation.advanceTimeBy(intervalCount, TimeUnit.SECONDS);

    // then:
    subscriber.assertError(NetworkPollingCall.MaxRetryException.class);
  }

  @Test
  public void ifConnectionCheckThrowsExceptions_throwsSameException() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(new RuntimeException());
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);

    // then:
    subscriber.assertError(RuntimeException.class);
  }

  @Test
  public void ifCallThrowsError_callsErrorFilterFunction() {
    final Exception exception = new RuntimeException();

    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(true);

      apiCall.call();
      returns(error(exception));
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);

    // then:
    new Verifications() {{
      errorCall.call(exception);
    }};
  }

  @Test
  public void ifCallThrowsUnignorableError_throwsSameError() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(true);

      apiCall.call();
      returns(error(new RuntimeException()));

      errorCall.call((Throwable) any);
      returns(false);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);

    // then:
    subscriber.assertError(RuntimeException.class);
  }

  @Test
  public void ifCallThrowsIgnorableErrorsTillTimeout_throwsTimeoutError() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(true);

      apiCall.call();
      returns(error(new RuntimeException()));

      errorCall.call((Throwable) any);
      returns(true);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);
    computation.advanceTimeBy(intervalCount, TimeUnit.SECONDS);

    // then:
    subscriber.assertError(NetworkPollingCall.MaxRetryException.class);
  }

  @Test
  public void ifNotConnectedOrCallThrowsIgnorableErrorsTillTimeout_throwsTimeoutError() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(false, true, false, true, false, true, false, true, false, false);

      apiCall.call();
      returns(error(new RuntimeException()));
      times = 4;

      errorCall.call((Throwable) any);
      returns(true);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);
    computation.advanceTimeBy(intervalCount, TimeUnit.SECONDS);

    // then:
    subscriber.assertError(NetworkPollingCall.MaxRetryException.class);
  }

  @Test
  public void whileCallIsBeingMade_noConnectionChecksAreMade() {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(true);
      times = 1; // assert that only called once!

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      returns(true);

      apiCall.call();
      returns(Observable
          .timer(intervalCount, TimeUnit.SECONDS, computation)
          .flatMap(instance(error(new RuntimeException())))
      );

      errorCall.call((Throwable) any);
      returns(true);
    }};

    // when:
    networkPollingCall
        .call()
        .subscribe(subscriber);
    computation.advanceTimeBy(intervalCount, TimeUnit.SECONDS);

    // then:
    // no assertions here!
  }

}