package au.com.umranium.espconnect.taskscreens.scanning;

import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.common.ScannedAccessPoint;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.taskscreens.common.BaseTaskController;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import au.com.umranium.espconnect.wifievents.WifiScanComplete;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Controller for the scanning task screen.
 */
public class ScanningController extends BaseTaskController<ScanningController.Surface> {

  private final WifiEvents wifiEvents;
  private final WifiManager wifiManager;
  private final Scheduler scheduler;
  private final ScannedAccessPointExtractor accessPointExtractor;
  private final EventTracker eventTracker;
  private final ErrorTracker errorTracker;
  private final String espSsidPattern;
  private Subscription scanningTask;

  @Inject
  public ScanningController(Surface surface, ScreenTracker screenTracker, WifiEvents wifiEvents, WifiManager wifiManager, Scheduler scheduler, ScannedAccessPointExtractor accessPointExtractor, EventTracker eventTracker, ErrorTracker errorTracker, @Named("EspSsidPattern") String espSsidPattern) {
    super(surface, screenTracker);
    this.wifiEvents = wifiEvents;
    this.wifiManager = wifiManager;
    this.scheduler = scheduler;
    this.accessPointExtractor = accessPointExtractor;
    this.eventTracker = eventTracker;
    this.errorTracker = errorTracker;
    this.espSsidPattern = espSsidPattern;
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
    showErrorScreen(R.string.no_course_location_permission_title, R.string.no_course_location_permission_msg);
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
        .map(new Func1<WifiScanComplete, List<ScannedAccessPoint>>() {
          @Override
          public List<ScannedAccessPoint> call(WifiScanComplete wifiScanComplete) {
            return accessPointExtractor.extract();
          }
        })
        .map(new FilterNonEsp(espSsidPattern))
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<List<ScannedAccessPoint>>() {
          @Override
          public void call(List<ScannedAccessPoint> accessPoints) {
            eventTracker.accessPointsSeen(accessPoints.size());
            if (accessPoints.isEmpty()) {
              surface.proceedWithNoAccessPoints();
            } else if (accessPoints.size()==1) {
              surface.proceedWithSingleAccessPoint(accessPoints.get(0));
            } else {
              surface.proceedWithAccessPoints(accessPoints);
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable e) {
            errorTracker.onException(e);
            showErrorScreen(R.string.scanning_generic_error_title, R.string.scanning_generic_error_msg);
          }
        });
  }

  public static class FilterNonEsp implements Func1<List<ScannedAccessPoint>, List<ScannedAccessPoint>> {

    private final String pattern;

    public FilterNonEsp(String pattern) {
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

  public interface Surface extends BaseTaskController.Surface {
    void proceedWithNoAccessPoints();

    void proceedWithSingleAccessPoint(ScannedAccessPoint accessPoint);

    void proceedWithAccessPoints(List<ScannedAccessPoint> accessPoints);
  }
}
