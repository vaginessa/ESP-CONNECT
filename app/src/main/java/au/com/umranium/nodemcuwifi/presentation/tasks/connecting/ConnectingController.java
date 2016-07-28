package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoint;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoints;
import au.com.umranium.nodemcuwifi.presentation.common.IsConnectedToEsp;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.utils.rx.ToVoid;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import rx.Observable;
import rx.Subscription;
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
      }
    }
  }

  @Override
  public void onStop() {
    stopMonitoring();
  }

  private void startMonitoring() {
    Observable<Void> initialEvent;
    if (wifiConnectionUtil.isAlreadyConnected()) {
      initialEvent = Observable.just((Void) null);
    } else {
      initialEvent = Observable.empty();
    }

    Observable<Void> connectedToEspEvents = wifiEvents
        .getConnected()
        .filter(new IsConnectedToEsp<WifiConnected>(wifiConnectionUtil))
        .map(ToVoid.<WifiConnected>getInstance());

    task = initialEvent
        .mergeWith(connectedToEspEvents)
        .first()
        .flatMap(new ScanApiCall(scheduler, serviceProvider))
        .map(new ReceivedToScannedAccessPoints())
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

  private static class ReceivedToScannedAccessPoints implements Func1<ReceivedAccessPoints, List<ScannedAccessPoint>> {
    @Override
    public List<ScannedAccessPoint> call(ReceivedAccessPoints receivedAccessPoints) {
      List<ScannedAccessPoint> scannedAccessPoints = new ArrayList<>(receivedAccessPoints.mAccessPoints.size());
      for (int i = 0; i < receivedAccessPoints.mAccessPoints.size(); i++) {
        ReceivedAccessPoint receivedAccessPoint = receivedAccessPoints.mAccessPoints.get(i);
        int signalStrength = Integer.parseInt(receivedAccessPoint.mQuality); // TODO: Get Ken to change this to an integer
        scannedAccessPoints.add(new ScannedAccessPoint(i, receivedAccessPoint.mSsid, signalStrength));
      }
      return scannedAccessPoints;
    }
  }

  private static class ScanApiCall implements Func1<Void, Observable<ReceivedAccessPoints>> {

    private Scheduler scheduler;
    private Provider<NodeMcuService> serviceProvider;

    public ScanApiCall(Scheduler scheduler, Provider<NodeMcuService> serviceProvider) {
      this.scheduler = scheduler;
      this.serviceProvider = serviceProvider;
    }

    @Override
    public Observable<ReceivedAccessPoints> call(Void aVoid) {
      return serviceProvider.get()
          .scan()
          .subscribeOn(scheduler.io());
    }
  }

}
