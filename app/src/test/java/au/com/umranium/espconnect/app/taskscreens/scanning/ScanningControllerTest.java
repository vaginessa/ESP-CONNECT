package au.com.umranium.espconnect.app.taskscreens.scanning;

import android.net.wifi.WifiManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.app.common.SerializableAction;
import au.com.umranium.espconnect.app.common.ToastDispatcher;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.taskscreens.utils.ScanAbilityUtil;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.wifievents.WifiEvent;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import au.com.umranium.espconnect.wifievents.WifiScanComplete;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

@RunWith(JMockit.class)
public class ScanningControllerTest {

  static final int SCAN_TIME_OUT = 10;
  static final String ESP_SSID_PATTERN = "ABC.*";
  static final String MATCHING_ESP_SSID = "ABC1";
  static final String UNMATCHING_ESP_SSID = "XYZ1";

  private final TestScheduler computation = new TestScheduler();
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
  ScanAbilityUtil scanAbilityUtil;
  @Injectable
  WifiManager wifiManager;
  @Injectable
  ScannedAccessPointExtractor accessPointExtractor;
  @Injectable
  EventTracker eventTracker;
  @Injectable
  ErrorTracker errorTracker;
  @Injectable
  ToastDispatcher toastDispatcher;
  @Injectable
  String espSsidPattern = ESP_SSID_PATTERN;
  @Injectable
  int scanTimeOutDurationMs = SCAN_TIME_OUT;
  @Tested
  ScanningController controller;

  // location permission actions
  List<SerializableAction<ScanningController>> permAcceptActions = new ArrayList<>();
  List<SerializableAction<ScanningController>> permDenyActions = new ArrayList<>();
  List<SerializableAction<ScanningController>> permPermDenyActions = new ArrayList<>();
  // wifi turn-on actions
  List<SerializableAction<ScanningController>> wifiAcceptActions = new ArrayList<>();
  List<SerializableAction<ScanningController>> wifiRejectActions = new ArrayList<>();
  // access-point turn-off actions
  List<SerializableAction<ScanningController>> apAcceptActions = new ArrayList<>();
  List<SerializableAction<ScanningController>> apRejectActions = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    new Expectations() {{
      scheduler.mainThread();
      returns(Schedulers.immediate());
      minTimes = 0;

      scheduler.computation();
      returns(computation);
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
  public void onStart_ensuresLocationPermission() {
    // given:

    // when:
    controller.onStart();

    // then:
    new Verifications() {{
      surface.ensureLocationPermissions(
          (SerializableAction<ScanningController>) any,
          (SerializableAction<ScanningController>) any,
          (SerializableAction<ScanningController>) any
      );
    }};
  }

  @Test
  public void startScanning() throws Exception {

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
  public void permanentlyDeniedLocationPermission_showsErrorScreen() {
    // given:

    // when:
    controller.handlePermanentlyDeniedLocationPermission();

    // then:
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
    }};
  }

  @Test
  public void locationPermissionGranted_checksAccessPointIsOff() throws Exception {
    // given:

    // when:
    controller.locationPermissionGranted();

    // then:
    new Verifications() {{
      scanAbilityUtil.isAccessPointOff();
    }};
  }

  @Test
  public void accessPointIsOn_requestUserToTurnOffAccessPoint() throws Exception {
    // given:

    // when:
    controller.accessPointIsOn();

    // then:
    new Verifications() {{
      surface.requestUserToTurnOffAccessPoint((SerializableAction<ScanningController>) any,
          (SerializableAction<ScanningController>) any);
    }};
  }

  @Test
  public void userAgreedToTurnOffAp_sendUserToWifiSettings() throws Exception {
    // given:

    // when:
    controller.userAgreedToTurnOffAp();

    // then:
    new Verifications() {{
      surface.sendUserToWifiSettings();
    }};
  }

  @Test
  public void userDisagreedToTurnOffAp_cancelTask() throws Exception {
    // given:

    // when:
    controller.userDisagreedToTurnOffAp();

    // then:
    new Verifications() {{
      surface.cancelTask();
    }};
  }

  @Test
  public void accessPointIsOff_isWifiOn() throws Exception {
    // given:

    // when:
    controller.accessPointIsOff();

    // then:
    new Verifications() {{
      scanAbilityUtil.isWifiOn();
    }};
  }

  @Test
  public void wifiIsOff_requestUserToTurnWifiOn() throws Exception {
    // given:

    // when:
    controller.wifiIsOff();

    // then:
    new Verifications() {{
      surface.requestUserToTurnWifiOn((SerializableAction<ScanningController>) any,
          (SerializableAction<ScanningController>) any);
    }};
  }

  @Test
  public void userAgreedToTurnWifiOn_triesToTurnWifiOn() throws Exception {
    // given:

    // when:
    controller.userAgreedToTurnWifiOn();

    // then:
    new Verifications() {{
      scanAbilityUtil.turnWifiOn();
    }};
  }

  @Test
  public void userAgreedToTurnWifiOn_wiFiTurnsOn_showsToast$TracksEvent() throws Exception {
    // given:
    new Expectations() {{
      scanAbilityUtil.turnWifiOn();
      returns(true);
    }};

    // when:
    controller.userAgreedToTurnWifiOn();

    // then:
    new Verifications() {{
      eventTracker.wifiCouldTurnOn();
      toastDispatcher.showShortToast(anyInt);
    }};
  }

  @Test
  public void userDisagreedToTurnWifiOn_cancelTask() throws Exception {
    // given:

    // when:
    controller.userDisagreedToTurnWifiOn();

    // then:
    new Verifications() {{
      surface.cancelTask();
    }};
  }

  @Test
  public void userAgreedToTurnWifiOn_wiFiDoesNotTurnsOn_showsToast$TracksEvent() throws Exception {
    // given:
    new Expectations() {{
      scanAbilityUtil.turnWifiOn();
      returns(false);
    }};

    // when:
    controller.userAgreedToTurnWifiOn();

    // then:
    new Verifications() {{
      eventTracker.wifiCouldNotTurnOn();
      toastDispatcher.showShortToast(anyInt);
    }};
  }

  @Test
  public void wifiIsOn_startsWifiScans() throws Exception {
    // given:

    // when:
    controller.wifiIsOn();

    // then:
    new Verifications() {{
      wifiEvents.getEvents();
    }};

  }

  @Test
  public void startScanning_startWifiScan() throws Exception {
    // given:

    // when:
    controller.startScanning();

    // then:
    new Verifications() {{
      wifiManager.startScan();
    }};
  }

  @Test
  public void startScanning_ifNoAccessPointsByTimeout_showsNoAccessPoints() throws Exception {
    // given:
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Arrays.asList(new ScannedAccessPoint(0, UNMATCHING_ESP_SSID, 0)));
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());
    computation.advanceTimeBy(SCAN_TIME_OUT, TimeUnit.MILLISECONDS);

    // then:
    new Verifications() {{
      surface.proceedWithNoAccessPoints();
    }};
  }

  @Test
  public void startScanning_ifNoAccessPointsBeforeTimeout_doesNotShowNoAccessPoints$initiatesAnotherScan() throws Exception {
    // given:
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Arrays.asList(new ScannedAccessPoint(0, UNMATCHING_ESP_SSID, 0)));
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());
    computation.advanceTimeBy(SCAN_TIME_OUT - 1, TimeUnit.MILLISECONDS);

    // then:
    new Verifications() {{
      surface.proceedWithNoAccessPoints();
      times = 0;

      wifiManager.startScan();
      times = 2;
    }};
  }

  @Test
  public void startScanning_ifSingleAccessPointIsFound_proceedWithSingleAccessPoint() throws Exception {
    // given:
    final ScannedAccessPoint accessPoint = new ScannedAccessPoint(0, MATCHING_ESP_SSID, 0);
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Arrays.asList(accessPoint));
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.proceedWithSingleAccessPoint(accessPoint);
    }};
  }

  @Test
  public void startScanning_ifMultipleAccessPointsAreFound_proceedWithMultipleAccessPoints() throws Exception {
    // given:
    final ScannedAccessPoint accessPoint1 = new ScannedAccessPoint(0, MATCHING_ESP_SSID, 0);
    final ScannedAccessPoint accessPoint2 = new ScannedAccessPoint(1, MATCHING_ESP_SSID, 0);
    new Expectations() {{
      accessPointExtractor.extract();
      returns(Arrays.asList(accessPoint1, accessPoint2));
    }};

    // when:
    controller.startScanning();
    wifiEventsSubject.onNext(WifiScanComplete.getInstance());

    // then:
    new Verifications() {{
      surface.proceedWithAccessPoints(Arrays.asList(accessPoint1, accessPoint2));
    }};
  }

}