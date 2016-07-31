package au.com.umranium.espconnect.presentation.tasks.configuring;

import android.util.Log;

import javax.inject.Inject;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.presentation.tasks.utils.NetworkPollingCall;
import au.com.umranium.espconnect.api.State;
import au.com.umranium.espconnect.presentation.common.ConfigDetails;
import au.com.umranium.espconnect.presentation.common.Scheduler;
import au.com.umranium.espconnect.presentation.tasks.common.BaseTaskController;
import au.com.umranium.espconnect.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.espconnect.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.espconnect.wifievents.WifiDisconnected;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Controller for the configuring task screen.
 */
public class ConfiguringController extends BaseTaskController<ConfiguringController.Surface> {

  private final WifiConnectionUtil wifiConnectionUtil;
  private final ConfigDetails configDetails;
  private final Scheduler scheduler;
  private final WifiEvents wifiEvents;
  private final NetworkPollingCall<Void> saveCall;
  private final NetworkPollingCall<State> stateCall;
  private Subscription task;

  @Inject
  public ConfiguringController(Surface surface, WifiConnectionUtil wifiConnectionUtil, ConfigDetails configDetails, Scheduler scheduler, WifiEvents wifiEvents, NetworkPollingCall<Void> saveCall, NetworkPollingCall<State> stateCall) {
    super(surface);
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.configDetails = configDetails;
    this.scheduler = scheduler;
    this.wifiEvents = wifiEvents;
    this.saveCall = saveCall;
    this.stateCall = stateCall;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.configuring_title);
    surface.setMessage(configDetails.getSsid());
  }

  @Override
  public void onStart() {
    if (!wifiConnectionUtil.isAlreadyConnected()) {
      surface.cancelTask();
    }

    if (task != null) {
      task.unsubscribe();
    }

    // when disconnected, reconnect
    Subscription reconnection = wifiEvents
        .getDisconnected()
        .subscribe(new Action1<WifiDisconnected>() {
          @Override
          public void call(WifiDisconnected wifiDisconnected) {
            try {
              wifiConnectionUtil.connectToNetwork();
            } catch (WifiConnectionException e) {
              surface.showErrorScreen(R.string.configuring_generic_error_title, e.getMessageId());
            }
          }
        });

    // configure and wait for ESP to connect or timeout
    Subscription configureAndWaitForConnect = saveCall
        .call()
        .take(1)
        .switchMap(new ToStateCall())
        .take(1)
        .map(new ConnectedToConfigNetwork())
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<Boolean>() {
          @Override
          public void call(Boolean connected) {
            if (connected) {
              surface.proceedToNextTask(configDetails.getSsid());
            } else {
              surface.showErrorScreen(R.string.configuring_generic_error_title, R.string.configuring_esp_unable_to_connect);
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable error) {
            if (!(error instanceof NetworkPollingCall.MaxRetryException)) {
              Log.e(ConfiguringController.class.getSimpleName(), "Error while configuring ESP8266", error);
              surface.showErrorScreen(R.string.configuring_generic_error_title, R.string.configuring_generic_error_msg);
            } else {
              surface.showErrorScreen(R.string.configuring_generic_error_title, R.string.configuring_connection_error_msg);
            }
          }
        });

    task = new CompositeSubscription(configureAndWaitForConnect, reconnection);
  }

  @Override
  public void onStop() {
    if (task != null) {
      task.unsubscribe();
      task = null;
    }
  }

  public interface Surface extends BaseTaskController.Surface {
    void setMessage(String networkName);

    void proceedToNextTask(String ssid);
  }

  private class ToStateCall implements Func1<Void, Observable<State>> {
    @Override
    public Observable<State> call(Void ignore) {
      return stateCall.call();
    }
  }

  private class ConnectedToConfigNetwork implements Func1<State, Boolean> {
    @Override
    public Boolean call(State state) {
      return configDetails.getSsid().equals(state.mSsid) && !state.isStationIpBlank();
    }
  }
}
