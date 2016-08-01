package au.com.umranium.espconnect.presentation.display.aplist;

import android.support.annotation.NonNull;

import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.presentation.common.BaseController;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.inject.Inject;
import java.util.List;

/**
 * Controller for {@link AccessPointListActivity}
 */
public class AccessPointListController extends BaseController<AccessPointListController.Surface> {

  private final PublishSubject<ScannedAccessPoint> accessPointClickEvents = PublishSubject.create();

  private final List<ScannedAccessPoint> accessPoints;

  @Inject
  public AccessPointListController(Surface surface, ScreenTracker screenTracker, List<ScannedAccessPoint> accessPoints) {
    super(surface, screenTracker);
    this.accessPoints = accessPoints;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startAccessPointList();

    surface.initListAdapter(accessPointClickEvents);

    accessPointClickEvents.subscribe(new Action1<ScannedAccessPoint>() {
      @Override
      public void call(@NonNull ScannedAccessPoint accessPoint) {
        onAccessPointClicked(accessPoint);
      }
    });

    surface.showAccessPoints(accessPoints);
  }

  private void onAccessPointClicked(ScannedAccessPoint accessPoint) {
    surface.proceedToNextTask(accessPoint);
  }

  public interface Surface extends BaseController.Surface {
    void initListAdapter(Observer<ScannedAccessPoint> accessPointClickObserver);

    void showAccessPoints(List<ScannedAccessPoint> accessPoints);

    void proceedToNextTask(ScannedAccessPoint accessPoint);
  }

}
