package au.com.umranium.espconnect.displayscreens.error;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.common.BaseController;

/**
 * Controller for the error screen.
 */
public class ErrorController extends BaseController<ErrorController.Surface> {

  private final String title;
  private final String description;

  @Inject
  public ErrorController(Surface surface, ScreenTracker screenTracker, @Named("title") String title, @Named("description") String description) {
    super(surface, screenTracker);
    this.title = title;
    this.description = description;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startError(title, description);
    surface.showScreen(title, description);
  }

  public void onOkBtnClicked() {
    surface.cancelTask();
  }

  public interface Surface extends BaseController.Surface {
    void showScreen(String title, String message);
  }
}
