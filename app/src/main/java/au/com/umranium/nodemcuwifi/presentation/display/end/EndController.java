package au.com.umranium.nodemcuwifi.presentation.display.end;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorController;

/**
 * Controller for the end screen.
 */
public class EndController extends BaseController<ErrorController.Surface> {

  public EndController(ErrorController.Surface surface) {
    super(surface);
  }

  public interface Surface extends BaseController.Surface {

  }

}
