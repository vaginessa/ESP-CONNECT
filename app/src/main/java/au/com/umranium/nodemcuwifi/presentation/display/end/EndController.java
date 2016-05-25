package au.com.umranium.nodemcuwifi.presentation.display.end;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorController;

import javax.inject.Inject;

/**
 * Controller for the end screen.
 */
public class EndController extends BaseController<EndController.Surface> {

  @Inject
  public EndController(Surface surface) {
    super(surface);
  }

  public interface Surface extends BaseController.Surface {

  }

}
