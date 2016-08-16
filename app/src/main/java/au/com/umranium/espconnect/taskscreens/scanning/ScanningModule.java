package au.com.umranium.espconnect.taskscreens.scanning;

import dagger.Module;
import dagger.Provides;

@Module
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
