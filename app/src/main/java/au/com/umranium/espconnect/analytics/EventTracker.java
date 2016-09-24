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

  public void accessPointsSeen(long count) {
    analytics.trackAction(R.string.analytics_event_scanned_access_points,
        R.string.analytics_event_scanned_access_points_results, count);
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

  public void userAccessPointIsOn() {
    analytics.trackAction(R.string.analytics_event_access_point_state,
        R.string.analytics_event_access_point_state_on);
  }

  public void userAccessPointIsOff() {
    analytics.trackAction(R.string.analytics_event_access_point_state,
        R.string.analytics_event_access_point_state_off);
  }

  public void userAgreedToTurnOffAccessPoint() {
    analytics.trackAction(R.string.analytics_event_access_point_state,
        R.string.analytics_event_access_point_state_agreed_turn_off);
  }

  public void userDisagreedToTurnOffAccessPoint() {
    analytics.trackAction(R.string.analytics_event_access_point_state,
        R.string.analytics_event_access_point_state_disagreed_to_turn_off);
  }

  public void userWifiOn() {
    analytics.trackAction(R.string.analytics_event_wifi_state,
        R.string.analytics_event_wifi_state_on);
  }

  public void userWifiOff() {
    analytics.trackAction(R.string.analytics_event_wifi_state,
        R.string.analytics_event_wifi_state_on);
  }

  public void userAgreedToTurnWifiOn() {
    analytics.trackAction(R.string.analytics_event_wifi_state,
        R.string.analytics_event_wifi_state_agreed_to_turn_on);
  }

  public void userDisgreedToTurnWifiOn() {
    analytics.trackAction(R.string.analytics_event_wifi_state,
        R.string.analytics_event_wifi_state_disagreed_to_turn_on);
  }

  public void wifiCouldTurnOn() {
    analytics.trackAction(R.string.analytics_event_wifi_state,
        R.string.analytics_event_wifi_state_could_turn_on);
  }

  public void wifiCouldNotTurnOn() {
    analytics.trackAction(R.string.analytics_event_wifi_state,
        R.string.analytics_event_wifi_state_could_not_turn_on);
  }

}
