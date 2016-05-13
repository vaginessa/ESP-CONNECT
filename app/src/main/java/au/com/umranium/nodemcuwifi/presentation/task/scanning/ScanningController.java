package au.com.umranium.nodemcuwifi.presentation.task.scanning;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;

/**
 * Controller for the scanning task screen.
 */
public class ScanningController extends BaseTaskController {

  public ScanningController(Surface surface) {
    super(surface);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.scanning_title);
    surface.setMessage(R.string.scanning_description);
  }

  @Override
  public void onStart() {

  }

  @Override
  public void onStop() {

  }

  @Override
  public void onDestroy() {

  }

  public interface Surface extends BaseTaskController.Surface {

  }
}
