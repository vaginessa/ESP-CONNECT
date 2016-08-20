package au.com.umranium.espconnect.app.taskscreens.scanning;

import javax.inject.Named;

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

  @Provides
  @Named("scanTimeOutDurationMs")
  public int provideScanTimeOutDurationMs() {
    return 10000;
  }

}
