package au.com.umranium.nodemcuwifi.presentation.display.aplist;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.display.end.EndActivity;
import au.com.umranium.nodemcuwifi.presentation.display.end.EndController;
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
  @ActivityScope
  public AccessPointListController.Surface provideSurface() {
    return activity;
  }

  @Provides
  @ActivityScope
  public List<ScannedAccessPoint> provideAccessPoints() {
    return accessPoints;
  }

}
