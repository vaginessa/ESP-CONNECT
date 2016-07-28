package au.com.umranium.nodemcuwifi.presentation.tasks.common;

import android.support.annotation.StringRes;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

/**
 * A controller (containing the logic) for the task activities.
 */
public abstract class BaseTaskController<SurfaceType extends BaseTaskController.Surface> extends BaseController<SurfaceType> {

  public BaseTaskController(SurfaceType surface) {
    super(surface);
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
