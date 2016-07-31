package au.com.umranium.espconnect.presentation.common;

/**
 * A generic controller (containing logic) for all activity controllers to inherit from.
 */
public abstract class BaseController<SurfaceType extends BaseController.Surface> {

  protected final SurfaceType surface;

  public BaseController(SurfaceType surface) {
    this.surface = surface;
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
  }
}
