package au.com.umranium.nodemcuwifi.wifievents;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * @author umran
 */
public final class WifiEvents {

  private static final String TAG = WifiEvents.class.getSimpleName();

  private static WifiEvents sInstance;

  public static synchronized WifiEvents getInstance() {
    if (sInstance == null) {
      sInstance = new WifiEvents();
    }
    return sInstance;
  }

  private PublishSubject<WifiEvent> mEvents;

  private WifiEvents() {
    mEvents = PublishSubject.create();
  }

  public Observable<WifiEvent> getEvents() {
    return mEvents;
  }

  public Observable<WifiConnectivityEvent> getConnectivityEvents() {
    return mEvents.ofType(WifiConnectivityEvent.class)
        .distinctUntilChanged();
  }

  public Observable<WifiEnabled> getEnabled() {
    return mEvents.ofType(WifiEnabled.class);
  }

  public Observable<WifiDisabled> getDisabled() {
    return mEvents.ofType(WifiDisabled.class);
  }

  public Observable<WifiConnected> getConnected() {
    return mEvents.ofType(WifiConnected.class);
  }

  public Observable<WifiDisconnected> getDisconnected() {
    return mEvents.ofType(WifiDisconnected.class);
  }

  public Observable<WifiStateEvent> getStateEvents() {
    return mEvents.ofType(WifiStateEvent.class);
  }

  public void emitEvent(WifiEvent event) {
    mEvents.onNext(event);
  }

}
