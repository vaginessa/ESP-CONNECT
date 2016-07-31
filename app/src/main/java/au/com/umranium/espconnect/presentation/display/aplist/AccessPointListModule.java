package au.com.umranium.espconnect.presentation.display.aplist;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;

import java.util.List;

@Module(includes = ActivityModule.class)
public class AccessPointListModule {

  private final AccessPointListActivity activity;
  private final List<ScannedAccessPoint> accessPoints;

  public AccessPointListModule(AccessPointListActivity activity, List<ScannedAccessPoint> accessPoints) {
    this.activity = activity;
    this.accessPoints = accessPoints;
  }

  @Provides
  public AccessPointListController.Surface provideSurface() {
    return activity;
  }

  @Provides
  public List<ScannedAccessPoint> provideAccessPoints() {
    return accessPoints;
  }

}
