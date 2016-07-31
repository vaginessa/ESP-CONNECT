package au.com.umranium.espconnect.presentation.tasks.connecting;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import au.com.umranium.espconnect.api.ReceivedAccessPoint;
import au.com.umranium.espconnect.api.ReceivedAccessPoints;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.presentation.common.Scheduler;
import au.com.umranium.espconnect.presentation.tasks.utils.NetworkPollingCall;
import au.com.umranium.espconnect.presentation.tasks.utils.WifiConnectionUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.Observable;
import rx.schedulers.Schedulers;

@RunWith(JMockit.class)
public class ConnectingControllerTest {

  @Injectable
  ConnectingController.Surface surface;
  @Injectable
  ScannedAccessPoint accessPoint;
  @Injectable
  WifiConnectionUtil wifiConnectionUtil;
  @Injectable
  Scheduler scheduler;
  @Injectable
  NetworkPollingCall<ReceivedAccessPoints> scanCall;
  @Tested
  ConnectingController controller;

  @Before
  public void setUp() throws Exception {
    new Expectations() {{
      scheduler.mainThread();
      result = Schedulers.immediate();
      minTimes = 0;

      scheduler.io();
      result = Schedulers.immediate();
      minTimes = 0;

      new MockUp<Log>() {
        @Mock
        int e(String tag, String msg, Throwable tr) {
          return 0;
        }
      };
    }};
  }

  @Test
  public void ifNotAlreadyConnected_connectsToNetwork() throws Exception {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = false;
    }};

    // when:
    controller.onStart();

    // then:
    new Verifications() {{
      wifiConnectionUtil.connectToNetwork();
      times = 1;
    }};
  }

  @Test
  public void afterFirstSuccessfulScanCall_proceedsToNext() throws Exception {
    final ReceivedAccessPoints receivedAccessPoints = createAccessPoints(
        new ReceivedAccessPoint("a", false, "1")
    );
    final List<ScannedAccessPoint> scannedAccessPoints = Arrays.asList(
        new ScannedAccessPoint(0, "a", 1)
    );

    // given:
    new Expectations() {{
      scanCall.call();
      returns(Observable.just(receivedAccessPoints));
    }};

    // when:
    controller.onStart();

    // then:
    new Verifications() {{
      //noinspection unchecked
      surface.proceedToNextTask(scannedAccessPoints);
    }};
  }

  @Test
  public void afterScanCallError_showsErrorMessage() throws Exception {
    // given:
    new Expectations() {{
      scanCall.call();
      returns(Observable.error(new RuntimeException()));
    }};

    // when:
    controller.onStart();

    // then:
    new Verifications() {{
      //noinspection unchecked
      surface.showErrorScreen(anyInt, anyInt);
    }};
  }

  private ReceivedAccessPoints createAccessPoints(ReceivedAccessPoint... accessPoints) {
    return new ReceivedAccessPoints(Arrays.asList(accessPoints));
  }
}