package au.com.umranium.nodemcuwifi.presentation.tasks.scanning;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class ScanningModule {

  private final ScanningActivity activity;

  public ScanningModule(ScanningActivity activity) {
    this.activity = activity;
  }

  @Provides
  public ScanningController.Surface provideSurface() {
    return activity;
  }

}
