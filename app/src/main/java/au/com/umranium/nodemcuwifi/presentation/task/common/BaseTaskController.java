package au.com.umranium.nodemcuwifi.presentation.task.common;

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
public abstract class BaseTaskController extends BaseController {

  private final Surface surface;
  private Subscription subscription;

  public BaseTaskController(Surface surface) {
    super(surface);
    this.surface = surface;
  }

  public void onStart() {
    // TODO: Remove this
    subscription = Observable
        .timer(2, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
            surface.proceedToNextTask();
          }
        });
  }

  @Override
  public void backPressed() {
    super.backPressed();
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }

  public void onStop() {
    subscription.unsubscribe();
  }

  public interface Surface extends BaseController.Surface {

    void setTitle(@StringRes int title);

    void setTitle(String title);

    void setMessage(@StringRes int message);

    void setMessage(String message);

    // TODO: Eventually remove this
    void proceedToNextTask();
  }
}
