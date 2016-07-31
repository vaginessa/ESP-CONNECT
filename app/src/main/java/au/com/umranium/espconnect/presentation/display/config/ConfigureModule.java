package au.com.umranium.espconnect.presentation.display.config;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;

import java.util.List;

@Module(includes = ActivityModule.class)
public class ConfigureModule {

  private final ConfigureActivity activity;
  private final ScannedAccessPoint accessPoint;
  private final List<ScannedAccessPoint> ssids;

  public ConfigureModule(ConfigureActivity activity,
                         ScannedAccessPoint accessPoint,
                         List<ScannedAccessPoint> ssids) {
    this.activity = activity;
    this.accessPoint = accessPoint;
    this.ssids = ssids;
  }

  @Provides
  public ConfigureController.Surface provideSurface() {
    return activity;
  }

  @Provides
  public ScannedAccessPoint provideAccessPoint() {
    return accessPoint;
  }

  @Provides
  public List<ScannedAccessPoint> provideSsids() {
    return ssids;
  }

}
