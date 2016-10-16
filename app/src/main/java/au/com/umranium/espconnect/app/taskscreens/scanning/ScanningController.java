package au.com.umranium.espconnect.app.taskscreens.scanning;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.app.common.SerializableAction;
import au.com.umranium.espconnect.app.common.ToastDispatcher;
import au.com.umranium.espconnect.app.common.WifiManagerSystemSurface;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskController;
import au.com.umranium.espconnect.app.taskscreens.utils.ScanAbilityUtil;
import au.com.umranium.espconnect.rx.CollectionNotEmpty;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.rx.TimeOut;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import au.com.umranium.espconnect.wifievents.WifiScanComplete;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Controller for the scanning task screen.
 */
class ScanningController extends BaseTaskController<ScanningController.Surface> {

  private final WifiEvents wifiEvents;
  private final WifiManagerSystemSurface wifiManager;
  private final Scheduler scheduler;
  private final ScanAbilityUtil scanAbilityUtil;
  private final ScannedAccessPointExtractor accessPointExtractor;
  private final EventTracker eventTracker;
  private final ErrorTracker errorTracker;
  private final ToastDispatcher toastDispatcher;
  private final String espSsidPattern;
  private final int scanTimeOutDurationMs;
  private Subscription scanningTask;

  @Inject
  ScanningController(Surface surface,
                     ScreenTracker screenTracker,
                     WifiEvents wifiEvents,
                     WifiManagerSystemSurface wifiManager,
                     Scheduler scheduler,
                     ScanAbilityUtil scanAbilityUtil,
                     ScannedAccessPointExtractor accessPointExtractor,
                     EventTracker eventTracker,
                     ErrorTracker errorTracker,
                     ToastDispatcher toastDispatcher,
                     @Named("EspSsidPattern") String espSsidPattern,
                     @Named("scanTimeOutDurationMs") int scanTimeOutDurationMs) {
    super(surface, screenTracker);
    this.wifiEvents = wifiEvents;
    this.wifiManager = wifiManager;
    this.scheduler = scheduler;
    this.scanAbilityUtil = scanAbilityUtil;
    this.accessPointExtractor = accessPointExtractor;
    this.eventTracker = eventTracker;
    this.errorTracker = errorTracker;
    this.toastDispatcher = toastDispatcher;
    this.espSsidPattern = espSsidPattern;
    this.scanTimeOutDurationMs = scanTimeOutDurationMs;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startScanning();
    surface.setTitle(R.string.scanning_title);
    surface.setMessage(R.string.scanning_description);
  }

  @Override
  public void onStart() {
    surface.ensureLocationPermissions(
      new UserGaveLocationPermissionAction(),
      new UserDeniedLocationPermissionAction(),
      new UserPermDeniedLocationPermissionAction());
  }

  private static class UserDeniedLocationPermissionAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.handleDeniedLocationPermission();
    }
  }

  private static class UserPermDeniedLocationPermissionAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.handlePermanentlyDeniedLocationPermission();
    }
  }

  @VisibleForTesting
  void handleDeniedLocationPermission() {
    eventTracker.locationPermissionRejected();
    handleNoLocationPermission();
  }

  @VisibleForTesting
  void handlePermanentlyDeniedLocationPermission() {
    eventTracker.locationPermissionDeniedPermanently();
    handleNoLocationPermission();
  }

  private void handleNoLocationPermission() {
    surface.cancelTask();
    showErrorScreen(R.string.no_course_location_permission_title, R.string.no_course_location_permission_msg);
  }

  private static class UserGaveLocationPermissionAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.locationPermissionGranted();
    }
  }

  @VisibleForTesting
  void locationPermissionGranted() {
    eventTracker.locationPermissionGiven();

    if (scanAbilityUtil.isAccessPointOff()) {
      accessPointIsOff();
    } else {
      accessPointIsOn();
    }
  }

  @VisibleForTesting
  void accessPointIsOn() {
    eventTracker.userAccessPointIsOn();
    surface.requestUserToTurnOffAccessPoint(
      new UserAcceptedTurnOffApAction(),
      new UserRejectedTurnOffApAction());
  }

  private static class UserAcceptedTurnOffApAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.userAgreedToTurnOffAp();
    }
  }

  private static class UserRejectedTurnOffApAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.userDisagreedToTurnOffAp();
    }
  }

  @VisibleForTesting
  void userAgreedToTurnOffAp() {
    eventTracker.userAgreedToTurnOffAccessPoint();
    surface.sendUserToWifiSettings();
  }

  @VisibleForTesting
  void userDisagreedToTurnOffAp() {
    eventTracker.userDisagreedToTurnOffAccessPoint();
    surface.cancelTask();
  }

  @VisibleForTesting
  void accessPointIsOff() {
    eventTracker.userAccessPointIsOff();
    if (scanAbilityUtil.isWifiOn()) {
      wifiIsOn();
    } else {
      wifiIsOff();
    }
  }

  @VisibleForTesting
  void wifiIsOff() {
    eventTracker.userWifiOff();
    surface.requestUserToTurnWifiOn(
      new UserAcceptedTurnWifiOnAction(),
      new UserRejectedTurnWifiOnAction());
  }

  private static class UserAcceptedTurnWifiOnAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.userAgreedToTurnWifiOn();
    }
  }

  private static class UserRejectedTurnWifiOnAction extends SerializableAction<ScanningController> {
    @Override
    public void run(ScanningController controller) {
      controller.userDisagreedToTurnWifiOn();
    }
  }

  @VisibleForTesting
  void userDisagreedToTurnWifiOn() {
    eventTracker.userDisgreedToTurnWifiOn();
    surface.cancelTask();
  }

  @VisibleForTesting
  void userAgreedToTurnWifiOn() {
    eventTracker.userAgreedToTurnWifiOn();
    if (scanAbilityUtil.turnWifiOn()) {
      eventTracker.wifiCouldTurnOn();
      toastDispatcher.showShortToast(R.string.wifi_turned_on);
      wifiIsOn();
    } else {
      eventTracker.wifiCouldNotTurnOn();
      toastDispatcher.showShortToast(R.string.wifi_could_not_be_turned_on);
    }
  }

  @VisibleForTesting
  void wifiIsOn() {
    eventTracker.userWifiOn();
    startScanning();
  }

  @VisibleForTesting
  void startScanning() {
    scanningTask = startWifiScans();
  }

  @Override
  public void onStop() {
    if (scanningTask != null) {
      scanningTask.unsubscribe();
      scanningTask = null;
    }
  }

  private Subscription startWifiScans() {
    return wifiEvents
      .getEvents()
      .ofType(WifiScanComplete.class)
      .doOnSubscribe(new Action0() {
        @Override
        public void call() {
          wifiManager.startScan();
        }
      })
      .map(new ExtractAccessPoints())
      .map(new FilterNonEsp(espSsidPattern))
      .doOnNext(new Action1<List<ScannedAccessPoint>>() {
        @Override
        public void call(List<ScannedAccessPoint> scannedAccessPoints) {
          if (scannedAccessPoints.isEmpty()) {
            wifiManager.startScan();
          }
        }
      })
      .filter(new CollectionNotEmpty<ScannedAccessPoint>())
      .compose(new TimeOut<List<ScannedAccessPoint>>(scanTimeOutDurationMs, TimeUnit.MILLISECONDS, scheduler.computation()))
      .observeOn(scheduler.mainThread())
      .subscribe(new Action1<List<ScannedAccessPoint>>() {
        @Override
        public void call(List<ScannedAccessPoint> accessPoints) {
          eventTracker.accessPointsSeen(accessPoints.size());
          if (accessPoints.isEmpty()) {
            throw new IllegalStateException("Invalid processing path for 0 access-points!");
          } else if (accessPoints.size() == 1) {
            surface.proceedWithSingleAccessPoint(accessPoints.get(0));
          } else {
            surface.proceedWithAccessPoints(accessPoints);
          }
        }
      }, new Action1<Throwable>() {
        @Override
        public void call(Throwable e) {
          if (e instanceof TimeOut.TimeOutException) {
            eventTracker.accessPointsSeen(0);
            surface.proceedWithNoAccessPoints();
          } else {
            errorTracker.onException(e);
            showErrorScreen(R.string.scanning_generic_error_title, R.string.scanning_generic_error_msg);
          }
        }
      });
  }

  private static class FilterNonEsp implements Func1<List<ScannedAccessPoint>, List<ScannedAccessPoint>> {

    private final String pattern;

    FilterNonEsp(String pattern) {
      this.pattern = pattern;
    }

    @Override
    public List<ScannedAccessPoint> call(List<ScannedAccessPoint> prevList) {
      List<ScannedAccessPoint> newList = new ArrayList<>(prevList.size());
      for (ScannedAccessPoint accessPoint : prevList) {
        if (accessPoint.getSsid().matches(pattern)) {
          newList.add(accessPoint);
        }
      }
      return newList;
    }
  }

  private class ExtractAccessPoints implements Func1<WifiScanComplete, List<ScannedAccessPoint>> {
    @Override
    public List<ScannedAccessPoint> call(WifiScanComplete wifiScanComplete) {
      return accessPointExtractor.extract();
    }
  }

  public interface Surface extends BaseTaskController.Surface {

    void ensureLocationPermissions(SerializableAction<ScanningController> onAcceptAction,
                                   SerializableAction<ScanningController> onDenyAction,
                                   SerializableAction<ScanningController> onPermDenyAction);

    void sendUserToWifiSettings();

    void requestUserToTurnWifiOn(SerializableAction<ScanningController> onAcceptAction,
                                 SerializableAction<ScanningController> onRejectAction);

    void requestUserToTurnOffAccessPoint(SerializableAction<ScanningController> onAcceptAction,
                                         SerializableAction<ScanningController> onRejectAction);

    void proceedWithNoAccessPoints();

    void proceedWithSingleAccessPoint(ScannedAccessPoint accessPoint);

    void proceedWithAccessPoints(List<ScannedAccessPoint> accessPoints);
  }
}
