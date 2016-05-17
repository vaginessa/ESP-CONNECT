package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;

/**
 * Controller for the configuring task screen.
 */
public class ConfiguringController extends BaseTaskController<ConfiguringController.Surface> {

  public ConfiguringController(Surface surface) {
    super(surface);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.configuring_title);
    surface.setMessage(R.string.configuring_description);
  }

  public interface Surface extends BaseTaskController.Surface {

  }
}
