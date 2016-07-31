package au.com.umranium.espconnect.presentation.display.welcome;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class WelcomeModule {

  private final WelcomeActivity activity;

  public WelcomeModule(WelcomeActivity activity) {
    this.activity = activity;
  }

  @Provides
  public WelcomeController.Surface provideSurface() {
    return activity;
  }

}
