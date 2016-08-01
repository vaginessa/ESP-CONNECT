package au.com.umranium.espconnect.presentation.tasks.common;

import android.support.annotation.StringRes;

import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.presentation.common.BaseController;

/**
 * A controller (containing the logic) for the task activities.
 */
public abstract class BaseTaskController<SurfaceType extends BaseTaskController.Surface> extends BaseController<SurfaceType> {

  public BaseTaskController(SurfaceType surface, ScreenTracker screenTracker) {
    super(surface, screenTracker);
  }

  public void onStart() {
  }

  public void onStop() {
  }

  @Override
  public void backPressed() {
    super.backPressed();
  }

  @Override
  public void nextTaskWasCancelled() {
    surface.cancelTask();
  }

  public void showErrorScreen(@StringRes int title, @StringRes int message) {
    surface.showErrorScreen(title, message);
  }

  public void showErrorScreen(String title, String message) {
    surface.showErrorScreen(title, message);
  }

  public interface Surface extends BaseController.Surface {

    void setTitle(@StringRes int title);

    void setTitle(String title);

    void setMessage(@StringRes int message);

    void setMessage(String message);

    void showErrorScreen(@StringRes int title, @StringRes int message);

    void showErrorScreen(String title, String message);
  }
}
