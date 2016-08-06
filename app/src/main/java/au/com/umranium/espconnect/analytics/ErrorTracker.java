package au.com.umranium.espconnect.analytics;

import javax.inject.Inject;

import au.com.umranium.espconnect.R;

/**
 * Tracks screen usage.
 */
public class ErrorTracker {

  private final Analytics analytics;

  @Inject
  public ErrorTracker(Analytics analytics) {
    this.analytics = analytics;
  }

  public void onException(Throwable e) {
    analytics.trackException(e);
  }

}
