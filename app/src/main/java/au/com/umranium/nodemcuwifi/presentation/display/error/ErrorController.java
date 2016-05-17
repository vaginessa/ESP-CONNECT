package au.com.umranium.nodemcuwifi.presentation.display.error;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;

/**
 * Controller for the error screen.
 */
public class ErrorController extends BaseController<ErrorController.Surface> {

  public ErrorController(Surface surface) {
    super(surface);
  }

  public void onOkBtnClicked() {
    surface.proceedToPrevScreen();
  }

  public interface Surface extends BaseController.Surface {
    void proceedToPrevScreen();
  }
}
