package au.com.umranium.espconnect.wifievents;

import javax.inject.Inject;

import au.com.umranium.espconnect.di.scope.AppScope;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * @author umran
 */
@AppScope
public final class WifiEvents {

  private PublishSubject<WifiEvent> mEvents;

  @Inject
  public WifiEvents() {
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
