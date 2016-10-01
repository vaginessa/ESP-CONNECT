package au.com.umranium.espconnect.app.displayscreens.end;

import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.app.BaseController;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Controller for the end screen.
 */
public class EndController extends BaseController<EndController.Surface> {

  private final String ssid;
  private final EventTracker eventTracker;

  @Inject
  public EndController(Surface surface, ScreenTracker screenTracker, @Named("ssid") String ssid, EventTracker eventTracker) {
    super(surface, screenTracker);
    this.ssid = ssid;
    this.eventTracker = eventTracker;
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
    eventTracker.endBackPressed();
    surface.finishTaskSuccessfully();
  }

  void onConfigureAnotherButtonPressed() {
    eventTracker.endConfigureAnother();
    surface.finishTaskSuccessfully();
  }

  void onExitButtonPressed() {
    eventTracker.endExit();
    surface.closeApp();
  }

  public interface Surface extends BaseController.Surface {
    void setDescription(String ssid);
  }

}
