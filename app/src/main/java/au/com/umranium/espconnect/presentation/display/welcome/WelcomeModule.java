package au.com.umranium.espconnect.presentation.display.welcome;

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
