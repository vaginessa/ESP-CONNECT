package au.com.umranium.espconnect.analytics;

import javax.inject.Inject;

import au.com.umranium.espconnect.R;

/**
 * Tracks analytic events.
 */
public class EventTracker {

  private final Analytics analytics;

  @Inject
  public EventTracker(Analytics analytics) {
    this.analytics = analytics;
  }

  public void locationPermissionGiven() {
    analytics.trackAction(R.string.analytics_event_permission_location,
        R.string.analytics_event_permission_given);
  }

  public void locationPermissionRejected() {
    analytics.trackAction(R.string.analytics_event_permission_location,
        R.string.analytics_event_permission_rejected);
  }

  public void locationPermissionDeniedPermanently() {
    analytics.trackAction(R.string.analytics_event_permission_location,
        R.string.analytics_event_permission_rejected_perm);
  }

}
