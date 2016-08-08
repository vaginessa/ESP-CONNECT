package au.com.umranium.espconnect.presentation.tasks.scanning;

import au.com.umranium.espconnect.di.activity.ActivityModule;
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
