package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.common.ConfigDetails;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
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
}
