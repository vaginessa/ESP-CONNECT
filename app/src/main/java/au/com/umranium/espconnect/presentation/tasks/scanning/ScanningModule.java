package au.com.umranium.espconnect.presentation.tasks.scanning;

import javax.inject.Named;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class ScanningModule {

  private static final String NODE_MCU_AP_FMT = ".*";
//  private static final String NODE_MCU_AP_FMT = "ESP.*";

  private final ScanningActivity activity;

  public ScanningModule(ScanningActivity activity) {
    this.activity = activity;
  }

  @Provides
  public ScanningController.Surface provideSurface() {
    return activity;
  }

  @Provides
  @Named("nodeAccessPointRegex")
  public String provideNodeAccessPointRegex() {
    return NODE_MCU_AP_FMT;
  }
}
