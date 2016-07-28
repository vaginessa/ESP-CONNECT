package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.State;
import au.com.umranium.nodemcuwifi.presentation.common.ConfigDetails;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.wifievents.WifiDisconnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

@RunWith(JMockit.class)
public class ConfiguringControllerTest {

  private static final String CONFIG_SSID = "SSID";
  private static final String CONFIG_IP = "1.1.1.1";
  private static final State CONNECTED_STATE = new State("", "", CONFIG_IP, "", false, CONFIG_SSID);
  private static final State DISCONNECTED_STATE = new State("", "", "0.0.0.0", "", false, "other");

  @Injectable
  ConfiguringController.Surface surface;
  @Injectable
  WifiConnectionUtil wifiConnectionUtil;
  @Injectable
  ConfigDetails configDetails;
  @Injectable
  Provider<NodeMcuService> serviceProvider;
  @Injectable
  NodeMcuService service;
  @Injectable
  Scheduler scheduler;

  WifiEvents wifiEvents;

  ConfiguringController controller;

  @Before
  public void setUp() throws Exception {
    new Expectations() {{
      configDetails.getSsid();
      result = CONFIG_SSID;
      minTimes = 0;

      configDetails.getPassword();
      result = "";
      minTimes = 0;

      serviceProvider.get();
      result = service;
      minTimes = 0;

      scheduler.mainThread();
      result = Schedulers.immediate();
      minTimes = 0;

      scheduler.io();
      result = Schedulers.immediate();
      minTimes = 0;

      new MockUp<Log>() {
        // Mock Log.e
        @Mock
        int e(String tag, String msg, Throwable tr) {
          return 0;
        }
      };
    }};

    wifiEvents = new WifiEvents();
    controller = new ConfiguringController(surface, wifiConnectionUtil, configDetails, serviceProvider, scheduler, wifiEvents);
  }

  @Test
  public void ifNotConnected_cancelsTask() {
    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = false;
    }};

    // when
    controller.onStart();

    // then
    new Verifications() {{
      surface.cancelTask();
    }};
  }


  @Test
  public void ifSaveThrowsError_cancelsTaskAndShowsError() {
    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = false;

      service.save(anyString, anyString);
      result = Observable.error(new RuntimeException());
    }};

    // when
    controller.onStart();

    // then
    new Verifications() {{
      surface.cancelTask();
      surface.showErrorMessage(R.string.configuring_generic_error);
    }};
  }

  @Test
  public void whenConnectedToAnotherNetwork_doesNotCallState() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = false;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;
      minTimes = 0;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(10, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      service.getState();
      times = 0;
    }};
  }

  @Test
  public void whenNotTrackingAWifiNetwork_doesNotCallState() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;
      minTimes = 0;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = false;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(10, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      service.getState();
      times = 0;
    }};
  }

  @Test
  public void whenConnectedToEspAndEspStateCallThrowsSocketError_doesNotThrowError() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      service.getState();
      returns(Observable.error(new SocketException()),
          Observable.error(new SocketTimeoutException()));

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(2, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      surface.proceedToNextTask();
      times = 0;

      surface.showErrorMessage(anyInt);
      times = 0;
    }};
  }

  @Test
  public void whenConnectedToEspAndEspStateCallThrowsNonSocketError_showsError() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      service.getState();
      result = Observable.error(new RuntimeException());

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(1, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      surface.proceedToNextTask();
      times = 0;

      surface.showErrorMessage(anyInt);
    }};
  }

  @Test
  public void whenConnectedToEspAndEspStateCallThrowsErrorThenSucceeds_eventuallyProceedsToNext() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      service.getState();
      returns(
          Observable.<State>error(new SocketException()),
          Observable.just(CONNECTED_STATE)
      );

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(2, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      surface.proceedToNextTask();
    }};
  }

  @Test
  public void whenConnectedToEspAndEspIsConnectedToAnotherNetwork_doesNotProceedToNext() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      service.getState();
      result = Observable.just(DISCONNECTED_STATE);

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(1, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      surface.proceedToNextTask();
      times = 0;
    }};
  }

  @Test
  public void whenNotConnectedPastThreshold_showsError() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      service.getState();
      result = Observable.just(DISCONNECTED_STATE);

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(ConfiguringController.TIMEOUT_SECONDS, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      surface.proceedToNextTask();
      times = 0;

      surface.showErrorMessage(anyInt);
    }};
  }

  @Test
  public void whenConnectedToEspAndEspConnectedToNetwork_proceedsToNext() {
    final TestScheduler computation = new TestScheduler();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;

      //noinspection CheckResult
      wifiConnectionUtil.isTrackingWifiNetwork();
      result = true;

      service.save(anyString, anyString);
      result = Observable.just((Void) null);
      minTimes = 0;

      service.getState();
      result = Observable.just(CONNECTED_STATE);

      scheduler.computation();
      result = computation;
      minTimes = 0;
    }};

    // when
    controller.onStart();
    computation.advanceTimeBy(1, TimeUnit.SECONDS);

    // then
    new Verifications() {{
      surface.proceedToNextTask();
    }};
  }

  @Test
  public void whenDisconnected_triesToReconnect() throws WifiConnectionException {
    // given

    // when
    controller.onStart();
    wifiEvents.emitEvent(WifiDisconnected.getInstance());

    // then
    new Verifications() {{
      wifiConnectionUtil.connectToNetwork();
    }};
  }

}