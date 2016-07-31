package au.com.umranium.espconnect.presentation.display.error;

import au.com.umranium.espconnect.presentation.common.BaseController;

import javax.inject.Inject;

/**
 * Controller for the error screen.
 */
public class ErrorController extends BaseController<ErrorController.Surface> {

  @Inject
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
