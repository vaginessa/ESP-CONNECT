package au.com.umranium.espconnect.app.displayscreens.end;

import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.app.BaseController;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Controller for the end screen.
 */
public class EndController extends BaseController<EndController.Surface> {

  private final String ssid;

  @Inject
  public EndController(Surface surface, ScreenTracker screenTracker, @Named("ssid") String ssid) {
    super(surface, screenTracker);
    this.ssid = ssid;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startEnd();
  }

  @Override
  public void onStart() {
    surface.setDescription(ssid);
  }

  @Override
  public void backPressed() {
    surface.finishTaskSuccessfully();
  }

  void onConfigureAnotherButtonPressed() {
    surface.finishTaskSuccessfully();
  }

  void onExitButtonPressed() {
    surface.closeApp();
  }

  public interface Surface extends BaseController.Surface {
    void setDescription(String ssid);
  }

}
