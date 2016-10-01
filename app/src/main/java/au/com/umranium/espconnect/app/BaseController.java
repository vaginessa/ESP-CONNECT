package au.com.umranium.espconnect.app;

import android.net.Uri;

import au.com.umranium.espconnect.analytics.ScreenTracker;

/**
 * A generic controller (containing logic) for all activity controllers to inherit from.
 */
public abstract class BaseController<SurfaceType extends BaseController.Surface> {

  protected final SurfaceType surface;
  protected final ScreenTracker screenTracker;

  public BaseController(SurfaceType surface, ScreenTracker screenTracker) {
    this.surface = surface;
    this.screenTracker = screenTracker;
  }

  public void onCreate() {
  }

  public void onStart() {
  }

  public void onStop() {
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

    void finishTaskSuccessfully();

    void cancelTask();

    void closeApp();

    void openUriInBrowser(Uri uri);

  }
}
