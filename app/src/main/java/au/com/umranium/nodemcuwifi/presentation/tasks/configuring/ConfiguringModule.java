package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.tasks.connecting.ConnectingActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.connecting.ConnectingController;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class ConfiguringModule {

  private final ConfiguringActivity activity;

  public ConfiguringModule(ConfiguringActivity activity) {
    this.activity = activity;
  }

  @Provides
  public ConfiguringController.Surface provideSurface() {
    return activity;
  }

}
