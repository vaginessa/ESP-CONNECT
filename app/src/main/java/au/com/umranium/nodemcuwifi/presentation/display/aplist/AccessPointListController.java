package au.com.umranium.nodemcuwifi.presentation.display.aplist;

import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.util.List;

/**
 * Controller for {@link AccessPointListActivity}
 */
public class AccessPointListController extends BaseController<AccessPointListController.Surface> {

  private final PublishSubject<ScannedAccessPoint> accessPointClickEvents = PublishSubject.create();

  private final List<ScannedAccessPoint> accessPoints;

  public AccessPointListController(AccessPointListController.Surface surface, List<ScannedAccessPoint> accessPoints) {
    super(surface);
    this.accessPoints = accessPoints;
  }

  @Override
  public void onCreate() {
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
