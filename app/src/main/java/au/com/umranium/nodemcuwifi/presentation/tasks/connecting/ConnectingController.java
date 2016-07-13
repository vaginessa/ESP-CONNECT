package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import javax.inject.Inject;

import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionException;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;

/**
 * Controller for the connecting task screen.
 */
public class ConnectingController extends BaseTaskController<ConnectingController.Surface> {

  private final ScannedAccessPoint accessPoint;
  private final WifiConnectionUtil wifiConnectionUtil;

  @Inject
  public ConnectingController(Surface surface,
                              ScannedAccessPoint accessPoint,
                              WifiConnectionUtil wifiConnectionUtil) {
    super(surface);
    this.accessPoint = accessPoint;
    this.wifiConnectionUtil = wifiConnectionUtil;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(accessPoint.getSsid());
    surface.setMessage(accessPoint.getSsid());
  }

  @Override
  public void onStart() {
    super.onStart();

    if (wifiConnectionUtil.isAlreadyConnected()) {
      try {
        wifiConnectionUtil.connectToNetwork();
      } catch (WifiConnectionException e) {

      }
    }
  }


  public interface Surface extends BaseTaskController.Surface {
    void setTitle(String accessPointName);

    void setMessage(String accessPointName);
  }
}
