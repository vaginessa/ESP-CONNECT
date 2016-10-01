package au.com.umranium.espconnect.app.taskscreens.configuring;

import android.util.Log;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.SocketException;
import java.util.List;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.api.data.State;
import au.com.umranium.espconnect.app.common.DisplayableError;
import au.com.umranium.espconnect.app.common.StringProvider;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowCheckingEspState;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowDone;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowSavingCredentials;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowTurnOffEspConfigMode;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.UpdateViewState;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.app.taskscreens.utils.NetworkPollingCall;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionException;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionUtil;
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
import rx.observers.TestSubscriber;
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
  ScreenTracker screenTracker;
  @Injectable
  ErrorTracker errorTracker;
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
  @Injectable
  NetworkPollingCall<Void> closeCall;
  @Injectable
  StringProvider stringProvider;
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
      errorTracker.onException((Throwable) any);
      surface.showErrorScreen(anyInt, -1);
    }};
  }

  @Test
  public void getConfigureWaitForConnectAndClose_firstEmitsShowSavingCredentials() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.empty());

      stateCall.call();
      returns(Observable.empty());
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    assertOnNextEventsOfType(subscriber.getOnNextEvents(), ShowSavingCredentials.class);
  }

  @Test
  public void getConfigureWaitForConnectAndClose_onSaveError_emitsDisplayableError() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.error(new RuntimeException()));

      stateCall.call();
      returns(Observable.empty());
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertError(DisplayableError.class);
  }

  @Test
  public void getConfigureWaitForConnectAndClose_onSave_ignoresSubsequentSaves_emitsShowCheckingEsp() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null, null));

      stateCall.call();
      returns(Observable.empty());
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    assertOnNextEventsOfType(subscriber.getOnNextEvents(), ShowSavingCredentials.class, ShowCheckingEspState.class);
  }

  @Test
  public void getConfigureWaitForConnectAndClose_onStateError_emitsDisplayableError() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.error(new RuntimeException()));
      minTimes = 0;
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    assertOnNextEventsOfType(subscriber.getOnNextEvents(), ShowSavingCredentials.class, ShowCheckingEspState.class);
    subscriber.assertError(DisplayableError.class);
  }

  @Test
  public void getConfigureWaitForConnectAndClose_onStateDisconnected_ignoresSubsequentStates_emitsDisplayableError() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.just(DISCONNECTED_STATE, CONNECTED_STATE));
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertError(DisplayableError.class);
    assertOnNextEventsOfType(subscriber.getOnNextEvents(), ShowSavingCredentials.class, ShowCheckingEspState.class);
  }


  @Test
  public void getConfigureWaitForConnectAndClose_onStateConnected_ignoresSubsequentStates_emitsShowTurnOffEspConfigMode() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.just(CONNECTED_STATE, DISCONNECTED_STATE));

      closeCall.call();
      returns(Observable.empty());
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    assertOnNextEventsOfType(subscriber.getOnNextEvents(), ShowSavingCredentials.class, ShowCheckingEspState.class, ShowTurnOffEspConfigMode.class);
  }

  @Test
  public void getConfigureWaitForConnectAndClose_onCloseError_emitsDisplayableError() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.just(CONNECTED_STATE));

      closeCall.call();
      returns(Observable.error(new RuntimeException()));
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertError(DisplayableError.class);
  }

  @Test
  public void getConfigureWaitForConnectAndClose_onClose_emitsShowDone() {
    // given
    TestSubscriber<UpdateViewState> subscriber = new TestSubscriber<>();
    new Expectations() {{
      saveCall.call();
      returns(Observable.just(null));

      stateCall.call();
      returns(Observable.just(CONNECTED_STATE));

      closeCall.call();
      returns(Observable.just(null));
    }};

    // when
    controller.getConfigureWaitForConnectAndClose().subscribe(subscriber);

    // then
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    assertOnNextEventsOfType(subscriber.getOnNextEvents(), ShowSavingCredentials.class, ShowCheckingEspState.class, ShowTurnOffEspConfigMode.class, ShowDone.class);
  }

  private static void assertOnNextEventsOfType(List<UpdateViewState> events, Class<? extends UpdateViewState> ... classes) {
    if (events.size()<classes.length) {
      throw new AssertionError("Too few events ("+events.size()+"<"+classes.length+")");
    }
    if (events.size()>classes.length) {
      throw new AssertionError("Too many events ("+events.size()+">"+classes.length+")");
    }
    for (int i = 0; i < events.size() && i < classes.length; i++) {
      if (events.get(i)==null) {
        throw new AssertionError("Event "+i+" is null");
      }
      if (!events.get(i).getClass().isAssignableFrom(classes[i])) {
        throw new AssertionError("Event " + i + " (" + events.get(i).getClass().getSimpleName() + ") is not assignable from " + classes[i].getSimpleName());
      }
    }
  }

}