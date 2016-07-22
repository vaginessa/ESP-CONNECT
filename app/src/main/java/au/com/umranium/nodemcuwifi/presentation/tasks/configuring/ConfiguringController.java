package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import javax.inject.Inject;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;

/**
 * Controller for the configuring task screen.
 */
public class ConfiguringController extends BaseTaskController<ConfiguringController.Surface> {

  private final WifiConnectionUtil wifiConnectionUtil;

  @Inject
  public ConfiguringController(Surface surface, WifiConnectionUtil wifiConnectionUtil) {
    super(surface);
    this.wifiConnectionUtil = wifiConnectionUtil;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.configuring_title);
    surface.setMessage(R.string.configuring_description);
  }

  @Override
  public void onStart() {
    if (!wifiConnectionUtil.isAlreadyConnected()) {
      surface.cancelTask();
    }
    surface.proceedToNextTask();
  }

  public interface Surface extends BaseTaskController.Surface {
    void proceedToNextTask();
  }
}
