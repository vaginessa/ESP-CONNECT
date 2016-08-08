package au.com.umranium.espconnect.presentation.tasks.scanning;

import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.presentation.common.Scheduler;
import au.com.umranium.espconnect.wifievents.WifiEvent;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import au.com.umranium.espconnect.wifievents.WifiScanComplete;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@RunWith(JMockit.class)
public class ScanningControllerTest {


  @Injectable
  ScanningController.Surface surface;
  @Injectable
  ScreenTracker screenTracker;
  @Injectable
  WifiEvents wifiEvents;
  PublishSubject<WifiEvent> wifiEventsSubject = PublishSubject.create();
  @Injectable
  Scheduler scheduler;
  @Injectable
  WifiManager wifiManager;
  @Injectable
  ScannedAccessPointExtractor accessPointExtractor;
  @Injectable
  EventTracker eventTracker;
  @Injectable
  ErrorTracker errorTracker;
  @Tested
  ScanningController controller;

  @Before
  public void setUp() throws Exception {
    new Expectations() {{
      scheduler.mainThread();
      returns(Schedulers.immediate());
      minTimes = 0;

      wifiEvents.getEvents();
      returns(wifiEventsSubject.asObservable());
      minTimes = 0;
    }};
  }

  @Test
  public void onCreate_tracksScreenEvent() {
    // given:

    // when:
    controller.onCreate();

    // then:
    new Verifications() {{
      screenTracker.startScanning();
    }};
  }

  @Test
  public void startScanning_tracksPermissionGivenEvent() {
    // given:

    // when:
    controller.startScanning();

    // then:
    new Verifications() {{
      eventTracker.locationPermissionGiven();
    }};
  }

  @Test
  public void startScanning_callsStartScan() {
    // given:

    // when:
    controller.startScanning();

    // then:
    new Verifications() {{
      wifiManager.startScan();
    }};
  }

  @Test
  public void startScanning_whenScanComplete_callsExtract() {
    // given:
    new Expectations() {{
      accessPointExtractor.extract();
      times = 1;
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
  }

  @Test
  public void startScanning_whenNoAccessPoints_proceedWithNoAccessPoints() {
    // given:
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Collections.emptyList());
      times = 1;
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.proceedWithNoAccessPoints();
    }};
  }

  @Test
  public void startScanning_whenHasAccessPoints_proceedWithAccessPoints() {
    // given:
    final ScannedAccessPoint accessPoint = new ScannedAccessPoint(0, "", 0);
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Collections.singletonList(accessPoint));
      times = 1;
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.proceedWithAccessPoints(Collections.singletonList(accessPoint));
    }};
  }

  @Test
  public void startScanning_onError_tracksErrorEvent() {
    // given:
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    final RuntimeException exception = new RuntimeException();
    new Expectations() {{
      wifiManager.startScan();
      returns(exception);
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      errorTracker.onException(withAny(exception));
    }};
  }

  @Test
  public void startScanning_onError_showsGenericErrorScreen() {
    // given:
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    final RuntimeException exception = new RuntimeException();
    new Expectations() {{
      wifiManager.startScan();
      returns(exception);
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
    }};
  }

  @Test
  public void handleDeniedLocationPermission_tracksPermissionRejectedEvent() {
    // given:

    // when:
    controller.handleDeniedLocationPermission();

    // then:
    new Verifications() {{
      eventTracker.locationPermissionRejected();
    }};
  }

  @Test
  public void handleDeniedLocationPermission_showsErrorScreen() {
    // given:

    // when:
    controller.handleDeniedLocationPermission();

    // then:
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
    }};
  }

  @Test
  public void handlePermanentlyDeniedLocationPermission_locationPermissionDeniedPermanently() {
    // given:

    // when:
    controller.handlePermanentlyDeniedLocationPermission();

    // then:
    new Verifications() {{
      eventTracker.locationPermissionDeniedPermanently();
    }};
  }

  @Test
  public void handlePermanentlyDeniedLocationPermission_showsErrorScreen() {
    // given:

    // when:
    controller.handlePermanentlyDeniedLocationPermission();

    // then:
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
    }};
  }
}