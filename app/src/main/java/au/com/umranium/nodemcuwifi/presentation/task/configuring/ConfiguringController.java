package au.com.umranium.nodemcuwifi.presentation.task.configuring;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;

/**
 * Controller for the configuring task screen.
 */
public class ConfiguringController extends BaseTaskController {

  private final Surface surface;

  public ConfiguringController(Surface surface) {
    super(surface);
    this.surface = surface;
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
