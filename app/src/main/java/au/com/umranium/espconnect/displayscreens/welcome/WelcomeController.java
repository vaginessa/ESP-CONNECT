package au.com.umranium.espconnect.displayscreens.welcome;

import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.common.BaseController;

import javax.inject.Inject;

/**
 * Controller for the welcome screen.
 */
public class WelcomeController extends BaseController<WelcomeController.Surface> {

  @Inject
  public WelcomeController(Surface surface, ScreenTracker screenTracker) {
    super(surface, screenTracker);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startWelcome();
  }

  public void onStartBtnClicked() {
    surface.proceedToNextScreen();
  }

  @Override
  public void nextTaskCompleted() {
    // do nothing
  }

  public interface Surface extends BaseController.Surface {
    void proceedToNextScreen();
  }
}
