package au.com.umranium.espconnect.app.taskscreens.configuring;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ErrorTracker;
import au.com.umranium.espconnect.analytics.LoggingErrorAction;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.api.data.State;
import au.com.umranium.espconnect.app.common.DisplayableError;
import au.com.umranium.espconnect.app.common.StringProvider;
import au.com.umranium.espconnect.app.common.ThrowableToDisplayableError;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskController;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowCheckingEspState;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowDone;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowSavingCredentials;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.ShowTurnOffEspConfigMode;
import au.com.umranium.espconnect.app.taskscreens.configuring.viewstate.UpdateViewState;
import au.com.umranium.espconnect.app.taskscreens.utils.NetworkPollingCall;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionException;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionUtil;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.rx.ToInstance;
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
  private final NetworkPollingCall<Void> closeCall;
  private final ErrorTracker errorTracker;
  private final StringProvider stringProvider;
  private Subscription task;

  @Inject
  public ConfiguringController(Surface surface, ScreenTracker screenTracker, WifiConnectionUtil wifiConnectionUtil,
                               ConfigDetails configDetails, Scheduler scheduler, WifiEvents wifiEvents,
                               @Named("save") NetworkPollingCall<Void> saveCall,
                               @Named("state") NetworkPollingCall<State> stateCall,
                               @Named("close") NetworkPollingCall<Void> closeCall,
                               ErrorTracker errorTracker,
                               StringProvider stringProvider) {
    super(surface, screenTracker);
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.configDetails = configDetails;
    this.scheduler = scheduler;
    this.wifiEvents = wifiEvents;
    this.saveCall = saveCall;
    this.stateCall = stateCall;
    this.closeCall = closeCall;
    this.stringProvider = stringProvider;
    this.errorTracker = errorTracker;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startConfiguring();
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
              errorTracker.onException(e);
              surface.showErrorScreen(R.string.configuring_generic_error_title, e.getMessageId());
            }
          }
        }, new LoggingErrorAction(errorTracker));

    // configure and wait for ESP to connect or timeout
    Subscription configureWaitForConnectAndClose = getConfigureWaitForConnectAndClose()
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<UpdateViewState>() {
          @Override
          public void call(UpdateViewState updateViewState) {
            updateViewState.apply(surface);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Log.e(ConfiguringController.class.getSimpleName(), "Error while configuring ESP8266", throwable);
            if (throwable instanceof DisplayableError) {
              DisplayableError displayableError = (DisplayableError) throwable;
              errorTracker.onException(displayableError.getCause());
              surface.showErrorScreen(stringProvider.getString(R.string.configuring_generic_error_title),
                  displayableError.getMessage());
            } else {
              errorTracker.onException(throwable);
              surface.showErrorScreen(R.string.configuring_generic_error_title, R.string.configuring_generic_error_msg);
            }
          }
        });

    task = new CompositeSubscription(configureWaitForConnectAndClose, reconnection);
  }

  @VisibleForTesting
  Observable<UpdateViewState> getConfigureWaitForConnectAndClose() {
    Observable<Void> save = saveCall.call().take(1);
    Observable<State> state = stateCall.call().take(1);
    return Observable
        .just((UpdateViewState) (new ShowSavingCredentials(stringProvider)))
        .mergeWith(
            save
                .onErrorResumeNext(ThrowableToDisplayableError.<Void>create(stringProvider.getString(R.string.configuring_esp_save_failed)))
                .map(ToInstance.instance((UpdateViewState) (new ShowCheckingEspState(stringProvider))))
                .mergeWith(
                    state
                        .onErrorResumeNext(ThrowableToDisplayableError.<State>create(stringProvider.getString(R.string.configuring_esp_state_check_failed)))
                        .switchMap(new Func1<State, Observable<UpdateViewState>>() {
                          @Override
                          public Observable<UpdateViewState> call(State state) {
                            boolean connected = configDetails.getSsid().equals(state.mSsid) && !state.isStationIpBlank();
                            if (connected) {
                              Observable<Void> close = closeCall.call().take(1);
                              return Observable.just((UpdateViewState) (new ShowTurnOffEspConfigMode(stringProvider)))
                                  .mergeWith(
                                      close
                                          .onErrorResumeNext(ThrowableToDisplayableError.<Void>create(stringProvider.getString(R.string.configuring_esp_close_failed)))
                                          .map(ToInstance.instance((new ShowDone(configDetails))))
                                  );
                            } else {
                              return Observable.error(new DisplayableError(stringProvider.getString(R.string.configuring_esp_unable_to_connect)));
                            }
                          }
                        })
                )
        );
  }

  @Override
  public void onStop() {
    if (task != null) {
      task.unsubscribe();
      task = null;
    }
  }

  public interface Surface extends BaseTaskController.Surface {
    void proceedToNextTask(String ssid);
  }

}
