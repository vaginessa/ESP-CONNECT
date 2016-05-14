package au.com.umranium.nodemcuwifi.presentation.welcome;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;

/**
 * Controller for the welcome screen.
 */
public class WelcomeController extends BaseController {

  private final Surface surface;

  public WelcomeController(Surface surface) {
    super(surface);
    this.surface = surface;
  }

  public void onStartBtnClicked() {
    surface.proceedToNextScreen();
  }

  public interface Surface extends BaseController.Surface {
    void proceedToNextScreen();
  }
}
