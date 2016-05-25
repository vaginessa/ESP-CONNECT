package au.com.umranium.nodemcuwifi.presentation.display.welcome;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;

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

  public interface Surface extends BaseController.Surface {
    void proceedToNextScreen();
  }
}
