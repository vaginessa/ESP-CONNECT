package au.com.umranium.espconnect.analytics;

import javax.inject.Inject;

import au.com.umranium.espconnect.R;

/**
 * Tracks screen usage.
 */
public class ScreenTracker {

  private final Analytics analytics;

  @Inject
  public ScreenTracker(Analytics analytics) {
    this.analytics = analytics;
  }

  public void leaveScreen() {
    analytics.leaveScreen();
  }

  public void startWelcome() {
    analytics.enterScreen(R.string.analytics_screen_welcome);
  }

  public void startScanning() {
    analytics.enterScreen(R.string.analytics_screen_scanning);
  }

  public void startAccessPointList() {
    analytics.enterScreen(R.string.analytics_screen_access_point_list);
  }

  public void startConnecting() {
    analytics.enterScreen(R.string.analytics_screen_connecting);
  }

  public void startConfigure() {
    analytics.enterScreen(R.string.analytics_screen_configure);
  }

  public void startConfiguring() {
    analytics.enterScreen(R.string.analytics_screen_configuring);
  }

  public void startEnd() {
    analytics.enterScreen(R.string.analytics_screen_end);
  }

  public void startError(String title, String description) {
    analytics.enterScreen(R.string.analytics_screen_error, "title='"+title+"', descr='"+description+"'");
  }
}
