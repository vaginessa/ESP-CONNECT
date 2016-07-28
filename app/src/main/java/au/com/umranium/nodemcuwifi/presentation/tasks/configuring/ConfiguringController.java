package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import android.support.annotation.StringRes;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.State;
import au.com.umranium.nodemcuwifi.presentation.common.ConfigDetails;
import au.com.umranium.nodemcuwifi.presentation.common.IsConnectedToEsp;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.utils.rx.LogThrowable;
import au.com.umranium.nodemcuwifi.utils.rx.ToInstance;
import au.com.umranium.nodemcuwifi.wifievents.WifiDisconnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
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
  private final NodeMcuService service;
  private final Scheduler scheduler;
  private final WifiEvents wifiEvents;
  private Subscription task;

  @Inject
  public ConfiguringController(Surface surface, WifiConnectionUtil wifiConnectionUtil, ConfigDetails configDetails, NodeMcuService service, Scheduler scheduler, WifiEvents wifiEvents) {
    super(surface);
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.configDetails = configDetails;
    this.service = service;
    this.scheduler = scheduler;
    this.wifiEvents = wifiEvents;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.configuring_title);
    surface.setMessage(R.string.configuring_description);
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
              surface.showErrorMessage(e.getMessageId());
              surface.cancelTask();
            }
          }
        });

    // once every second, when connected to the ESP, wait for the ESP is connected to the required network
    Observable<State> waitForEspConnectedState = Observable
        .interval(1, TimeUnit.SECONDS, scheduler.computation())
        .onBackpressureDrop()
        .filter(new IsConnectedToEsp<Long>(wifiConnectionUtil))
        .flatMap(new MapToStateCall<Long>(scheduler, service))
        .filter(new Func1<State, Boolean>() {
          @Override
          public Boolean call(State state) {
            return configDetails.getSsid().equals(state.mSsid);
          }
        });

    // configure and wait for ESP to connect
    Subscription configureAndWaitForConnect = service
        .save(configDetails.getSsid(), configDetails.getPassword())
        .subscribeOn(scheduler.io())
        .switchMap(ToInstance.getInstance(waitForEspConnectedState))
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<State>() {
          @Override
          public void call(State state) {
            surface.proceedToNextTask();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable error) {
            Log.e(ConfiguringController.class.getSimpleName(), "Error while configuring ESP8266", error);
            surface.showErrorMessage(R.string.configuring_generic_error);
            surface.cancelTask();
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
    void showErrorMessage(@StringRes int message);

    void proceedToNextTask();
  }

  private static class MapToStateCall<T> implements Func1<T, Observable<State>> {

    private Scheduler scheduler;
    private NodeMcuService service;

    public MapToStateCall(Scheduler scheduler, NodeMcuService service) {
      this.scheduler = scheduler;
      this.service = service;
    }

    @Override
    public Observable<State> call(T ignored) {
      return service
          .getState()
          .subscribeOn(scheduler.io())
          .doOnError(new LogThrowable(ConfiguringController.class.getSimpleName(), "Error retrieving ESP's state"))
          .onErrorResumeNext(Observable.<State>empty()); // avoid throwing errors
    }
  }

}
