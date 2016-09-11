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
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

@RunWith(JMockit.class)
public class ScanningControllerTest {

  public static final int SCAN_TIME_OUT = 10;
  public static final String ESP_SSID_PATTERN = "ABC.*";
  public static final String MATCHING_ESP_SSID = "ABC1";
  public static final String UNMATCHING_ESP_SSID = "XYZ1";

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
  public void onStart_callsEnsureLocationPermissionGiven() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));
    }};

    // when:
    controller.onStart();

    // then:
    Assert.assertEquals("Number of permission accepted actions", permAcceptActions.size(), 1);
    Assert.assertEquals("Number of permission denied actions", permDenyActions.size(), 1);
    Assert.assertEquals("Number of permission permanently denied actions", permPermDenyActions.size(), 1);
  }

  @Test
  public void locationPermissionGranted_tracksPermissionGivenEvent() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      eventTracker.locationPermissionGiven();
    }};
  }

  @Test
  public void locationPermissionGranted_checksWhetherWifiIsOn() {
    // given:
    new Expectations() {{
      //noinspection unchecked
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      scanAbilityUtil.isWifiOn();
      times = 1;
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsNotOn_requestsUserToTurnWifiOn() {
    // given:
    new Expectations() {{
      scanAbilityUtil.isWifiOn();
      returns(false);

      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      //noinspection ConstantConditions
      surface.requestUserToTurnWifiOn(
          (ScanningController.UserAcceptedTurnWifiOnAction) any,
          (ScanningController.UserRejectedTurnWifiOnAction) any);
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsNotOnAndUserRejectsToTurnOn_cancelsTask() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(false);

      surface.requestUserToTurnWifiOn(withCapture(wifiAcceptActions), withCapture(wifiRejectActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    wifiRejectActions.get(0).run(controller);

    // then:
    new Verifications() {{
      surface.cancelTask();
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsNotOnAndUserAcceptsToTurnOn_triesToTurnOnWifi() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(false);

      surface.requestUserToTurnWifiOn(withCapture(wifiAcceptActions), withCapture(wifiRejectActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    wifiAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      scanAbilityUtil.turnWifiOn();
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsNotOnAndSuccessfullyTurnsOn_showsShortToast() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(false);

      surface.requestUserToTurnWifiOn(withCapture(wifiAcceptActions), withCapture(wifiRejectActions));

      scanAbilityUtil.turnWifiOn();
      returns(true);
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    wifiAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      toastDispatcher.showShortToast(R.string.wifi_turned_on);
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsNotOnAndFailsToTurnsOn_showsShortToast() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(false);

      surface.requestUserToTurnWifiOn(withCapture(wifiAcceptActions), withCapture(wifiRejectActions));

      scanAbilityUtil.turnWifiOn();
      returns(false);
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    wifiAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      toastDispatcher.showShortToast(R.string.wifi_could_not_be_turned_on);
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsNotOnAndSuccessfullyTurnsOn_checksWhetherAccessPointIsOff() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(false);

      surface.requestUserToTurnWifiOn(withCapture(wifiAcceptActions), withCapture(wifiRejectActions));

      scanAbilityUtil.turnWifiOn();
      returns(true);
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    wifiAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      scanAbilityUtil.isAccessPointOff();
    }};
  }

  @Test
  public void locationPermissionGranted_ifWifiIsOn_checksWhetherAccessPointIsOff() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(true);
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      scanAbilityUtil.isAccessPointOff();
    }};
  }

  @Test
  public void permissionDenied_showsErrorScreenAndTracksPermissionRejectedEvent() {
    // given:
    new Expectations() {{
      //noinspection unchecked
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));
    }};

    // when:
    controller.onStart();
    permDenyActions.get(0).run(controller);

    // then:
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
      eventTracker.locationPermissionRejected();
    }};
  }

  @Test
  public void permanentlyDeniedLocationPermission_showsErrorScreenAndLocationPermissionDeniedPermanently() {
    // given:
    new Expectations() {{
      //noinspection unchecked
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));
    }};

    // when:
    controller.onStart();
    permPermDenyActions.get(0).run(controller);

    // then:
    new Verifications() {{
      surface.showErrorScreen(anyInt, anyInt);
      eventTracker.locationPermissionDeniedPermanently();
    }};
  }

  @Test
  public void wifiIsOn_ifAccessPointIsOnAndUserAcceptsToTurnItOff_sendUserToWifiSettings() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(true);

      scanAbilityUtil.isAccessPointOff();
      returns(false);

      surface.askUserToTurnOffAccessPoint(withCapture(apAcceptActions), withCapture(apRejectActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    apAcceptActions.get(0).run(controller);

    // then:
    new Verifications() {{
      surface.sendUserToWifiSettings();
    }};
  }

  @Test
  public void wifiIsOn_ifAccessPointIsOnAndUserAcceptsToTurnItOff_cancelsTask() {
    // given:
    new Expectations() {{
      surface.ensureLocationPermissions(withCapture(permAcceptActions), withCapture(permDenyActions), withCapture(permPermDenyActions));

      scanAbilityUtil.isWifiOn();
      returns(true);

      scanAbilityUtil.isAccessPointOff();
      returns(false);

      surface.askUserToTurnOffAccessPoint(withCapture(apAcceptActions), withCapture(apRejectActions));
    }};

    // when:
    controller.onStart();
    permAcceptActions.get(0).run(controller);
    apRejectActions.get(0).run(controller);

    // then:
    new Verifications() {{
      surface.cancelTask();
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
  public void startScanning_ifScanDoesNotCompleteBeforeTimeOut_showsErrorScreenAndTracksException() {
    // given:
    new Expectations() {{
      accessPointExtractor.extract();
      times = 0;
    }};

    // when:
    controller.startScanning();
    computation.advanceTimeBy(SCAN_TIME_OUT, TimeUnit.MILLISECONDS);

    // then:
    new Verifications() {{
      errorTracker.onException(withAny(new TimeoutException()));
      times = 1;

      surface.showErrorScreen(anyInt, anyInt);
      times = 1;
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
  public void startScanning_onError_showsGenericErrorScreenAndTracksErrorEvent() {
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
      surface.showErrorScreen(anyInt, anyInt);
    }};
  }

}