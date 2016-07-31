package au.com.umranium.espconnect.presentation.tasks.configuring;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.SocketException;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.api.State;
import au.com.umranium.espconnect.presentation.common.ConfigDetails;
import au.com.umranium.espconnect.presentation.common.Scheduler;
import au.com.umranium.espconnect.presentation.tasks.utils.NetworkPollingCall;
import au.com.umranium.espconnect.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.espconnect.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.espconnect.wifievents.WifiDisconnected;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

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
  Scheduler scheduler;
  @Injectable
  WifiEvents wifiEvents;
  @Injectable
  NetworkPollingCall<Void> saveCall;
  @Injectable
  NetworkPollingCall<State> stateCall;
  @Tested
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
  public void whenDisconnected_triesToReconnect() throws WifiConnectionException {
    final PublishSubject<WifiDisconnected> subject = PublishSubject.create();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiEvents.getDisconnected();
      returns(subject.asObservable());
    }};

    // when
    controller.onStart();
    subject.onNext(WifiDisconnected.getInstance());

    // then
    new Verifications() {{
      wifiConnectionUtil.connectToNetwork();
    }};
  }

  @Test
  public void ifConnectThrowsError_showsErrorScreen() throws WifiConnectionException {
    final PublishSubject<WifiDisconnected> subject = PublishSubject.create();

    // given
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(false);

      wifiEvents.getDisconnected();
      returns(subject.asObservable());

      wifiConnectionUtil.connectToNetwork();
      result = new WifiConnectionException(-1);
    }};

    // when
    controller.onStart();
    subject.onNext(WifiDisconnected.getInstance());

    // then
    new Verifications() {{
      surface.showErrorScreen(anyInt, -1);
    }};
  }

  @Test
  public void afterFirstSave_ignoresSubsequentSaves() {
    // given
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null, null));
    }};

    // when
    controller.onStart();

    // then
    new Verifications() {{
      stateCall.call();
      times = 1;
    }};
  }

  @Test
  public void ifSaveCallThrowsError_showsErrorMessage() {
    // given
    new Expectations() {{
      saveCall.call();
      returns(Observable.error(new RuntimeException()));
    }};

    // when
    controller.onStart();

    // then
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
      times = 1;
    }};
  }

  @Test
  public void ifStateCallThrowsError_showsErrorMessage() {
    // given
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.error(new RuntimeException()));
    }};

    // when
    controller.onStart();

    // then
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
      times = 1;
    }};
  }

  @Test
  public void ifStateCallThrowsTimeoutError_showsTimeoutErrorMessage() {
    // given
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.error(new NetworkPollingCall.MaxRetryException(new SocketException())));
    }};

    // when
    controller.onStart();

    // then
    new Verifications() {{
      surface.showErrorScreen(anyInt, R.string.configuring_connection_error_msg);
      times = 1;
    }};
  }

  @Test
  public void afterDisconnectedState_ignoresDisconnectedStates() {
    // given
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.just(DISCONNECTED_STATE));
    }};

    // when
    controller.onStart();


    // then
    new Verifications() {{
      surface.proceedToNextTask(anyString);
      times = 0;
    }};
  }

  @Test
  public void afterConnectedState_proceedsToNext() {
    // given
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.just(CONNECTED_STATE));
    }};

    // when
    controller.onStart();


    // then
    new Verifications() {{
      surface.proceedToNextTask(anyString);
    }};
  }

}