package au.com.umranium.nodemcuwifi.presentation.task.connecting;

import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;

/**
 * Controller for the connecting task screen.
 */
public class ConnectingController extends BaseTaskController {

  private final Surface surface;
  private final ScannedAccessPoint accessPoint;

  public ConnectingController(Surface surface, ScannedAccessPoint accessPoint) {
    super(surface);
    this.surface = surface;
    this.accessPoint = accessPoint;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(accessPoint.getSsid());
    surface.setMessage(accessPoint.getSsid());
  }

  public interface Surface extends BaseTaskController.Surface {
    void setTitle(String accessPointName);
    void setMessage(String accessPointName);
  }
}
