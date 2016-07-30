package au.com.umranium.nodemcuwifi.presentation.display.end;

import javax.inject.Named;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorActivity;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorController;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class EndModule {

  private final EndActivity activity;
  private final String ssid;

  public EndModule(EndActivity activity, String ssid) {
    this.activity = activity;
    this.ssid = ssid;
  }

  @Provides
  public EndController.Surface provideSurface() {
    return activity;
  }

  @Provides
  @Named("ssid")
  public String provideSsid() {
    return ssid;
  }

}
