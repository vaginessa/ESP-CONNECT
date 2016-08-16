package au.com.umranium.espconnect.taskscreens.configuring;

import javax.inject.Named;
import javax.inject.Provider;

import au.com.umranium.espconnect.api.IgnoreSocketErrorFunction;
import au.com.umranium.espconnect.api.NodeMcuService;
import au.com.umranium.espconnect.api.State;
import au.com.umranium.espconnect.api.calls.SaveCall;
import au.com.umranium.espconnect.api.calls.StateCall;
import au.com.umranium.espconnect.common.ConfigDetails;
import au.com.umranium.espconnect.common.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

@Module
public class ConfiguringModule {

  private final ConfiguringActivity activity;
  private final ScannedAccessPoint scannedAccessPoint;
  private final ConfigDetails configDetails;

  public ConfiguringModule(ConfiguringActivity activity, ScannedAccessPoint scannedAccessPoint, ConfigDetails configDetails) {
    this.activity = activity;
    this.scannedAccessPoint = scannedAccessPoint;
    this.configDetails = configDetails;
  }

  @Provides
  public ConfiguringController.Surface provideSurface() {
    return activity;
  }

  @Provides
  public ScannedAccessPoint provideScannedAccessPoint() {
    return scannedAccessPoint;
  }

  @Provides
  public ConfigDetails provideConfigDetails() {
    return configDetails;
  }

  @Provides
  @Named("intervalSeconds")
  public long providePollingIntervalSeconds() {
    return 1L;
  }

  @Provides
  @Named("maxCallCount")
  public int providePollingMaxCallCount() {
    return 10;
  }

  @Provides
  public Func1<Throwable, Boolean> providePollingErrorFunction() {
    return new IgnoreSocketErrorFunction();
  }

  @Provides
  public Func0<Observable<State>> providePollingStateCall(Provider<NodeMcuService> serviceProvider) {
    return new StateCall(serviceProvider);
  }

  @Provides
  public Func0<Observable<Void>> providePollingSaveCall(Provider<NodeMcuService> serviceProvider) {
    return new SaveCall(serviceProvider, configDetails.getSsid(), configDetails.getPassword());
  }

}
