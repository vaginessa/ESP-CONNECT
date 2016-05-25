package au.com.umranium.nodemcuwifi.presentation.display.error;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class ErrorModule {

  private final ErrorActivity activity;

  public ErrorModule(ErrorActivity activity) {
    this.activity = activity;
  }

  @Provides
  @ActivityScope
  public ErrorController.Surface provideSurface() {
    return activity;
  }

}
