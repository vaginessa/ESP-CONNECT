package au.com.umranium.espconnect.app.taskscreens.connecting;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.api.data.ReceivedAccessPoint;
import au.com.umranium.espconnect.api.data.ReceivedAccessPoints;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskController;
import au.com.umranium.espconnect.app.taskscreens.utils.NetworkPollingCall;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionException;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionUtil;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Controller for the connecting task screen.
 */
public class ConnectingController extends BaseTaskController<ConnectingController.Surface> {

  private final ScannedAccessPoint accessPoint;
  private final WifiConnectionUtil wifiConnectionUtil;
  private final Scheduler scheduler;
  private final NetworkPollingCall<ReceivedAccessPoints> scanCall;
  private final ErrorTracker errorTracker;
  private Subscription task;

  @Inject
  public ConnectingController(Surface surface, ScreenTracker screenTracker, ScannedAccessPoint accessPoint, WifiConnectionUtil wifiConnectionUtil, Scheduler scheduler, NetworkPollingCall<ReceivedAccessPoints> scanCall, ErrorTracker errorTracker) {
    super(surface, screenTracker);
    this.accessPoint = accessPoint;
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.scheduler = scheduler;
    this.scanCall = scanCall;
    this.errorTracker = errorTracker;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startConnecting();
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
        errorTracker.onException(e);
        surface.showErrorScreen(R.string.connecting_generic_error_title, e.getMessageId());
      }
    }
  }

  @Override
  public void onStop() {
    stopMonitoring();
  }

  private void startMonitoring() {
    task = scanCall.call()
        .take(1)
        .map(new ReceivedToScannedAccessPoints())
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<List<ScannedAccessPoint>>() {
          @Override
          public void call(List<ScannedAccessPoint> scannedAccessPoints) {
            surface.proceedToNextTask(scannedAccessPoints);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable error) {
            errorTracker.onException(error);
            if (!(error instanceof NetworkPollingCall.MaxRetryException)) {
              Log.e(ConnectingController.class.getSimpleName(), "Error while connecting to ESP8266", error);
              surface.showErrorScreen(R.string.connecting_generic_error_title, R.string.connecting_generic_error_msg);
            } else {
              surface.showErrorScreen(R.string.connecting_generic_error_title, R.string.connecting_connection_error_msg);
            }
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

}
