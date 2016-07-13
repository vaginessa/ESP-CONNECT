package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityModule.class)
public class ConnectingModule {

  private final ConnectingActivity activity;
  private final ScannedAccessPoint scannedAccessPoint;

  public ConnectingModule(ConnectingActivity activity, ScannedAccessPoint scannedAccessPoint) {
    this.activity = activity;
    this.scannedAccessPoint = scannedAccessPoint;
  }

  @Provides
  public ConnectingController.Surface provideSurface() {
    return activity;
  }

  @Provides
  public ScannedAccessPoint provideScannedAccessPoint() {
    return scannedAccessPoint;
  }

}
