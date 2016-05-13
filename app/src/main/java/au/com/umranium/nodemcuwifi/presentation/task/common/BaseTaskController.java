package au.com.umranium.nodemcuwifi.presentation.task.common;

import android.support.annotation.StringRes;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

/**
 * A controller (containing the logic) for the task activities.
 */
public abstract class BaseTaskController {

  protected final Surface surface;
  private Subscription subscription;

  public BaseTaskController(Surface surface) {
    this.surface = surface;
  }

  public void onCreate() {
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

  public void onStop() {
    subscription.unsubscribe();
  }

  public void onDestroy() {

  }

  public void backPressed() {
    surface.cancelTask();
  }

  public void nextTaskWasCancelled() {

  }

  public void nextTaskCompleted() {
    surface.finishTaskSuccessfully();
  }

  public interface Surface {

    void setTitle(@StringRes int title);

    void setMessage(@StringRes int message);

    void proceedToNextTask();

    void finishTaskSuccessfully();

    void cancelTask();
  }
}
