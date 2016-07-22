package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoint;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoints;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.utils.rx.Pred;
import au.com.umranium.nodemcuwifi.utils.rx.ToVoid;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Controller for the connecting task screen.
 */
public class ConnectingController extends BaseTaskController<ConnectingController.Surface> {

  private final ScannedAccessPoint accessPoint;
  private final WifiEvents wifiEvents;
  private final WifiConnectionUtil wifiConnectionUtil;
  private final Provider<NodeMcuService> serviceProvider;
  private final Scheduler scheduler;
  private Subscription task;

  @Inject
  public ConnectingController(Surface surface, ScannedAccessPoint accessPoint, WifiEvents wifiEvents,
                              WifiConnectionUtil wifiConnectionUtil, Provider<NodeMcuService> serviceProvider,
                              Scheduler scheduler) {
    super(surface);
    this.accessPoint = accessPoint;
    this.wifiEvents = wifiEvents;
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.serviceProvider = serviceProvider;
    this.scheduler = scheduler;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(accessPoint.getSsid());
    surface.setMessage(accessPoint.getSsid());
  }

  @Override
  public void onStart() {
    startMonitoring();

    if (!wifiConnectionUtil.isAlreadyConnected()) {
      try {
        wifiConnectionUtil.connectToNetwork();
      } catch (WifiConnectionException e) {
        surface.showErrorMessage(e.getMessageId());
        surface.cancelTask();
        stopMonitoring();
      }
    }
  }

  @Override
  public void onStop() {
    stopMonitoring();
  }

  private void startMonitoring() {
    Pred<Void> onlyWhenConnected = new Pred<Void>() {
      @Override
      public Boolean call(Void aVoid) {
        return wifiConnectionUtil.isAlreadyConnected();
      }
    };

    Observable<Void> initialEvent;
    if (wifiConnectionUtil.isAlreadyConnected()) {
      initialEvent = Observable.just((Void) null);
    } else {
      initialEvent = Observable.empty();
    }

    Func1<Void, Observable<ReceivedAccessPoints>> scan = new Func1<Void, Observable<ReceivedAccessPoints>>() {
      @Override
      public Observable<ReceivedAccessPoints> call(Void aVoid) {
        return serviceProvider.get().scan();
      }
    };

    Func1<ReceivedAccessPoints, List<ScannedAccessPoint>> toScannedAccessPoints = new Func1<ReceivedAccessPoints, List<ScannedAccessPoint>>() {
      @Override
      public List<ScannedAccessPoint> call(ReceivedAccessPoints receivedAccessPoints) {
        List<ScannedAccessPoint> scannedAccessPoints = new ArrayList<>(receivedAccessPoints.mAccessPoints.size());
        for (int i = 0; i < receivedAccessPoints.mAccessPoints.size(); i++) {
          ReceivedAccessPoint receivedAccessPoint = receivedAccessPoints.mAccessPoints.get(i);
          scannedAccessPoints.add(new ScannedAccessPoint("00:00:00", receivedAccessPoint.mSsid, 0));
        }
        return scannedAccessPoints;
      }
    };

    Observable<Void> wifiConnectionEvents = wifiEvents
        .getConnectivityEvents()
        .ofType(WifiConnected.class)
        .map(ToVoid.<WifiConnected>getInstance())
        .filter(onlyWhenConnected);

    task = initialEvent
        .mergeWith(wifiConnectionEvents)
        .first()
        .flatMap(scan)
        .map(toScannedAccessPoints)
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<List<ScannedAccessPoint>>() {
          @Override
          public void call(List<ScannedAccessPoint> scannedAccessPoints) {
            surface.proceedToNextTask(scannedAccessPoints);
          }
        });
  }

  private void stopMonitoring() {
    if (task != null) {
      task.unsubscribe();
      task = null;
    }
  }

  public interface Surface extends BaseTaskController.Surface {
    void setTitle(String accessPointName);

    void setMessage(String accessPointName);

    void showErrorMessage(@StringRes int message);

    void proceedToNextTask(List<ScannedAccessPoint> accessPoints);
  }
}
