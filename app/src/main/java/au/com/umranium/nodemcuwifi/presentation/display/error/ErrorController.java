package au.com.umranium.nodemcuwifi.presentation.display.error;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;

/**
 * Controller for the error screen.
 */
public class ErrorController extends BaseController {

  private final Surface surface;

  public ErrorController(Surface surface) {
    super(surface);
    this.surface = surface;
  }

  public void onOkBtnClicked() {
    surface.proceedToPrevScreen();
  }

  public interface Surface extends BaseController.Surface {
    void proceedToPrevScreen();
  }
}
