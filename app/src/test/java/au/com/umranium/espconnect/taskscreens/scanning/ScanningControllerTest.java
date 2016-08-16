package au.com.umranium.espconnect.taskscreens.scanning;

import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.common.ScannedAccessPoint;
import au.com.umranium.espconnect.common.Scheduler;
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

  public static final String ESP_SSID_PATTERN = "ABC.*";
  public static final String MATCHING_ESP_SSID = "ABC1";
  public static final String UNMATCHING_ESP_SSID = "XYZ1";

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
  @Injectable
  String espSsidPattern = ESP_SSID_PATTERN;
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
  public void startScanning_whenUnmatchingAccessPoint2_filtersUnmatchingAccessPoints() {
    final ScannedAccessPoint unmatchingAccessPoint1 = new ScannedAccessPoint(1, UNMATCHING_ESP_SSID, 0);
    final ScannedAccessPoint unmatchingAccessPoint2 = new ScannedAccessPoint(2, UNMATCHING_ESP_SSID, 0);

    // given:
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Arrays.asList(unmatchingAccessPoint1, unmatchingAccessPoint2));
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
  public void startScanning_whenHasSingleAccessPoint_proceedWithAccessPoints() {
    // given:
    final ScannedAccessPoint matchingAccessPoint1 = new ScannedAccessPoint(0, MATCHING_ESP_SSID, 0);

    new Expectations() {{
      accessPointExtractor.extract();
      returns(Collections.singletonList(matchingAccessPoint1));
      times = 1;
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.proceedWithSingleAccessPoint(matchingAccessPoint1);
    }};
  }

  @Test
  public void startScanning_whenHasMultipleAccessPoints_proceedWithAccessPoints() {
    // given:
    final ScannedAccessPoint matchingAccessPoint1 = new ScannedAccessPoint(0, MATCHING_ESP_SSID, 0);
    final ScannedAccessPoint matchingAccessPoint2 = new ScannedAccessPoint(1, MATCHING_ESP_SSID, 0);

    new Expectations() {{
      accessPointExtractor.extract();
      returns(Arrays.asList(matchingAccessPoint1, matchingAccessPoint2));
      times = 1;
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.proceedWithAccessPoints(Arrays.asList(matchingAccessPoint1, matchingAccessPoint2));
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