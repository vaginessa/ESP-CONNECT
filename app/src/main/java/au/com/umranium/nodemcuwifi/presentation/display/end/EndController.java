package au.com.umranium.nodemcuwifi.presentation.display.end;

import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorController;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Controller for the end screen.
 */
public class EndController extends BaseController<EndController.Surface> {

  private final String ssid;

  @Inject
  public EndController(Surface surface, @Named("ssid") String ssid) {
    super(surface);
    this.ssid = ssid;
  }

  @Override
  public void onStart() {
    surface.setDescription(ssid);
  }

  public interface Surface extends BaseController.Surface {
    void setDescription(String ssid);
  }

}
