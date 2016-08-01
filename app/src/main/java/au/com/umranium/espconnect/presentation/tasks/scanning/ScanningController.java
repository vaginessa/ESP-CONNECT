package au.com.umranium.espconnect.presentation.tasks.scanning;

import android.net.wifi.WifiManager;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.presentation.common.ToastDispatcher;
import au.com.umranium.espconnect.presentation.tasks.common.BaseTaskController;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import au.com.umranium.espconnect.wifievents.WifiScanComplete;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.List;

import javax.inject.Inject;

/**
 * Controller for the scanning task screen.
 */
public class ScanningController extends BaseTaskController<ScanningController.Surface> {

  private final WifiEvents wifiEvents;
  private final WifiManager wifiManager;
  private final ScannedAccessPointExtractor accessPointExtractor;
  private final ToastDispatcher toastDispatcher;
  private final EventTracker eventTracker;
  private Subscription scanningTask;
  private long lastScanRequestTimestamp = -1;

  @Inject
  public ScanningController(Surface surface, ScreenTracker screenTracker, WifiEvents wifiEvents, WifiManager wifiManager, ToastDispatcher toastDispatcher, ScannedAccessPointExtractor accessPointExtractor, EventTracker eventTracker) {
    super(surface, screenTracker);
    this.wifiEvents = wifiEvents;
    this.wifiManager = wifiManager;
    this.toastDispatcher = toastDispatcher;
    this.accessPointExtractor = accessPointExtractor;
    this.eventTracker = eventTracker;
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
    // ignore
  }

  public void startScanning() {
    eventTracker.locationPermissionGiven();
    scanningTask = startWifiScans();
  }

  public void handleDeniedLocationPermission() {
    eventTracker.locationPermissionRejected();
    handleNoLocationPermission();
  }

  public void handlePermanentlyDeniedLocationPermission() {
    eventTracker.locationPermissionDeniedPermanently();
    handleNoLocationPermission();
  }

  private void handleNoLocationPermission() {
    surface.cancelTask();
    toastDispatcher.showLongToast(R.string.message_no_course_location_permission);
  }

  @Override
  public void onStop() {
    if (scanningTask!=null) {
      scanningTask.unsubscribe();
    }
  }

  private Subscription startWifiScans() {
    return getAccessPoints()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<ScannedAccessPoint>>() {
          @Override
          public void call(List<ScannedAccessPoint> accessPoints) {
            if (accessPoints.isEmpty()) {
              surface.proceedWithNoAccessPoints();
            } else {
              surface.proceedWithAccessPoints(accessPoints);
            }
          }
        });
  }

  private Observable<List<ScannedAccessPoint>> getAccessPoints() {
    return wifiEvents
        .getEvents()
        .ofType(WifiScanComplete.class)
        .doOnSubscribe(new Action0() {
          @Override
          public void call() {
            startScan();
          }
        })
        .map(new Func1<WifiScanComplete, List<ScannedAccessPoint>>() {
          @Override
          public List<ScannedAccessPoint> call(WifiScanComplete wifiScanComplete) {
            return accessPointExtractor.extract(lastScanRequestTimestamp);
          }
        });
  }

  private void startScan() {
    wifiManager.startScan();
    lastScanRequestTimestamp = System.currentTimeMillis();
  }

  public interface Surface extends BaseTaskController.Surface {
    void proceedWithAccessPoints(List<ScannedAccessPoint> accessPoints);

    void proceedWithNoAccessPoints();
  }
}
