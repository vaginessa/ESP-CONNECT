package au.com.umranium.espconnect.presentation.display.error;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.di.scope.ActivityScope;
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
