package au.com.umranium.espconnect.app.taskscreens.connecting;

import javax.inject.Named;
import javax.inject.Provider;

import au.com.umranium.espconnect.api.IgnoreSocketErrorFunction;
import au.com.umranium.espconnect.api.NodeMcuService;
import au.com.umranium.espconnect.api.data.ReceivedAccessPoints;
import au.com.umranium.espconnect.api.calls.ScanCall;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

@Module
public class ConnectingModule {

  private final ConnectingActivity activity;
  private final ScannedAccessPoint scannedAccessPoint;

  public ConnectingModule(ConnectingActivity activity, ScannedAccessPoint scannedAccessPoint) {
    this.activity = activity;
    this.scannedAccessPoint = scannedAccessPoint;
  }

  @Provides
  public ConnectingController.Surface provideSurface() {
    return activity;
  }

  @Provides
  public ScannedAccessPoint provideScannedAccessPoint() {
    return scannedAccessPoint;
  }

  @Provides
  @Named("intervalSeconds")
  public long providePollingIntervalSeconds() {
    return 1L;
  }

  @Provides
  @Named("maxCallCount")
  public int providePollingMaxCallCount() {
    return 60;
  }

  @Provides
  public Func1<Throwable, Boolean> providePollingErrorFunction() {
    return new IgnoreSocketErrorFunction();
  }

  @Provides
  public Func0<Observable<ReceivedAccessPoints>> providePollingScanCall(Provider<NodeMcuService> serviceProvider) {
    return new ScanCall(serviceProvider);
  }


}
