package au.com.umranium.nodemcuwifi.presentation.display.end;

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

  public EndModule(EndActivity activity) {
    this.activity = activity;
  }

  @Provides
  @ActivityScope
  public EndController.Surface provideSurface() {
    return activity;
  }

  @Provides
  @ActivityScope
  public BaseActivity provideBaseActivity() {
    return activity;
  }

}
