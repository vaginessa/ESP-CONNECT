package au.com.umranium.espconnect.presentation.display.end;

import javax.inject.Named;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import dagger.Module;
import dagger.Provides;

@Module
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
