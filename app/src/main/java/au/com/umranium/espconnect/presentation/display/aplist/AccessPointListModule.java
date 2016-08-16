package au.com.umranium.espconnect.presentation.display.aplist;

import java.util.List;

import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import dagger.Module;
import dagger.Provides;

@Module
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
