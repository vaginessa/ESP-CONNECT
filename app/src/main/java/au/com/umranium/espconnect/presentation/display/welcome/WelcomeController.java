package au.com.umranium.espconnect.presentation.display.welcome;

import au.com.umranium.espconnect.presentation.common.BaseController;

import javax.inject.Inject;

/**
 * Controller for the welcome screen.
 */
public class WelcomeController extends BaseController<WelcomeController.Surface> {

  @Inject
  public WelcomeController(Surface surface) {
    super(surface);
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
