package au.com.umranium.espconnect.app.taskscreens.configuring;

import javax.inject.Named;
import javax.inject.Provider;

import au.com.umranium.espconnect.api.IgnoreSocketErrorFunction;
import au.com.umranium.espconnect.api.NodeMcuService;
import au.com.umranium.espconnect.api.calls.CloseCall;
import au.com.umranium.espconnect.api.calls.SaveCall;
import au.com.umranium.espconnect.api.calls.StateCall;
import au.com.umranium.espconnect.api.data.State;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.taskscreens.utils.NetworkPollingCall;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionUtil;
import au.com.umranium.espconnect.rx.Scheduler;
import dagger.Module;
import dagger.Provides;
import rx.functions.Func1;

@Module
public class ConfiguringModule {

  private static final long POLLING_INTERVAL_SECONDS = 1L;
  private static final int POLLING_MAX_COUNT = 10;

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
  @Named("state")
  public NetworkPollingCall<State> providePollingStateCall(WifiConnectionUtil wifiConnectionUtil, Scheduler scheduler, Provider<NodeMcuService> serviceProvider) {
    return new NetworkPollingCall<>(POLLING_INTERVAL_SECONDS, POLLING_MAX_COUNT, wifiConnectionUtil, scheduler,
        new StateCall(serviceProvider), new IgnoreSocketErrorFunction());
  }

  @Provides
  @Named("save")
  public NetworkPollingCall<Void> providePollingSaveCall(WifiConnectionUtil wifiConnectionUtil, Scheduler scheduler, Provider<NodeMcuService> serviceProvider) {
    return new NetworkPollingCall<>(POLLING_INTERVAL_SECONDS, POLLING_MAX_COUNT, wifiConnectionUtil, scheduler,
        new SaveCall(serviceProvider, configDetails.getSsid(), configDetails.getPassword()), new IgnoreSocketErrorFunction());
  }

  @Provides
  @Named("close")
  public NetworkPollingCall<Void> providePollingCloseCall(WifiConnectionUtil wifiConnectionUtil, Scheduler scheduler, Provider<NodeMcuService> serviceProvider) {
    return new NetworkPollingCall<>(POLLING_INTERVAL_SECONDS, POLLING_MAX_COUNT, wifiConnectionUtil, scheduler,
        new CloseCall(serviceProvider), new IgnoreSocketErrorFunction());
  }
}
