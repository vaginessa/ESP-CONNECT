package au.com.umranium.nodemcuwifi.presentation.task.connecting;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;

/**
 * Controller for the connecting task screen.
 */
public class ConnectingController extends BaseTaskController {

  public ConnectingController(Surface surface) {
    super(surface);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.connecting_title);
    surface.setMessage(R.string.connecting_description);
  }

  public interface Surface extends BaseTaskController.Surface {

  }
}
