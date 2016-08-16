package au.com.umranium.espconnect.displayscreens.welcome;

import dagger.Module;
import dagger.Provides;

@Module
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
