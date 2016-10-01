package au.com.umranium.espconnect.app.displayscreens.welcome;

import android.net.Uri;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.EventTracker;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.app.BaseController;
import au.com.umranium.espconnect.app.common.StringProvider;

import javax.inject.Inject;

/**
 * Controller for the welcome screen.
 */
class WelcomeController extends BaseController<WelcomeController.Surface> {

  private final EventTracker eventTracker;
  private final StringProvider stringProvider;

  @Inject
  WelcomeController(Surface surface, ScreenTracker screenTracker,
                    EventTracker eventTracker, StringProvider stringProvider) {
    super(surface, screenTracker);
    this.eventTracker = eventTracker;
    this.stringProvider = stringProvider;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startWelcome();
  }

  void onStartBtnClicked() {
    surface.proceedToNextScreen();
  }

  void onMoreAppInfoTxtClicked() {
    Uri uri = Uri.parse(stringProvider.getString(R.string.project_url));
    surface.openUriInBrowser(uri);
    eventTracker.welcomeFindMoreAppInfo();
  }

  @Override
  public void nextTaskCompleted() {
    // do nothing
  }

  public interface Surface extends BaseController.Surface {
    void proceedToNextScreen();
  }
}
