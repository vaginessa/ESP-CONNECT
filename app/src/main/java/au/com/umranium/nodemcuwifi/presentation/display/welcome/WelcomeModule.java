package au.com.umranium.nodemcuwifi.presentation.display.welcome;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class WelcomeModule {

  private final WelcomeActivity activity;

  public WelcomeModule(WelcomeActivity activity) {
    this.activity = activity;
  }

  @Provides
  @ActivityScope
  public WelcomeController.Surface provideSurface() {
    return activity;
  }

  @Provides
  @ActivityScope
  public BaseActivity provideBaseActivity() {
    return activity;
  }

}
