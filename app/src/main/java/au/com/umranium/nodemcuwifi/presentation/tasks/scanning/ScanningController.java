package au.com.umranium.nodemcuwifi.presentation.tasks.scanning;

import android.net.wifi.WifiManager;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.utils.rx.ToInstance;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the scanning task screen.
 */
public class ScanningController extends BaseTaskController {

  // TODO: Change this to ESP.*
  private static final String NODE_MCU_AP_FMT = ".*";

  private final Surface surface;
  private final WifiEvents wifiEvents;
  private final WifiManager wifiManager;
  private final ScannedAccessPointExtractor accessPointExtractor;
  private Subscription scanningTask;
  private long lastScanRequestTimestamp = -1;

  public ScanningController(Surface surface, WifiEvents wifiEvents, WifiManager wifiManager) {
    super(surface);
    this.surface = surface;
    this.wifiEvents = wifiEvents;
    this.wifiManager = wifiManager;
    this.accessPointExtractor = new ScannedAccessPointExtractor(wifiManager, NODE_MCU_AP_FMT);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.scanning_title);
    surface.setMessage(R.string.scanning_description);
  }

  @Override
  public void onStart() {
    scanningTask = startWifiScans();
  }

  @Override
  public void onStop() {
    scanningTask.unsubscribe();
  }

  private Subscription startWifiScans() {
    return getAccessPoints()
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
    return Observable.timer(2, TimeUnit.SECONDS)
        .map(ToInstance.getInstance(Arrays.asList(
            new ScannedAccessPoint("00:00", "a", 1),
            new ScannedAccessPoint("00:01", "b", 2)
        )))
//        .map(ToInstance.getInstance(Collections.<ScannedAccessPoint>emptyList()))
        .observeOn(AndroidSchedulers.mainThread());
//    return wifiEvents
//        .getEvents()
//        .ofType(WifiScanComplete.class)
//        .doOnSubscribe(new Action0() {
//          @Override
//          public void call() {
//            startScan();
//          }
//        })
//        .map(new Func1<WifiScanComplete, List<ScannedAccessPoint>>() {
//          @Override
//          public List<ScannedAccessPoint> call(WifiScanComplete wifiScanComplete) {
//            return accessPointExtractor.extract(lastScanRequestTimestamp);
//          }
//        });
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
