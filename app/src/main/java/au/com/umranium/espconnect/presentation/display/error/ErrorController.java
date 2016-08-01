package au.com.umranium.espconnect.presentation.display.error;

import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.presentation.common.BaseController;

import javax.inject.Inject;

/**
 * Controller for the error screen.
 */
public class ErrorController extends BaseController<ErrorController.Surface> {

  @Inject
  public ErrorController(Surface surface, ScreenTracker screenTracker) {
    super(surface, screenTracker);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startError();
  }

  public void onOkBtnClicked() {
    surface.proceedToPrevScreen();
  }

  public interface Surface extends BaseController.Surface {
    void proceedToPrevScreen();
  }
}
