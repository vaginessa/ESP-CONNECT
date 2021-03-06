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

  public void welcomeFindMoreAppInfo() {
    analytics.trackAction(R.string.analytics_event_welcome,
        R.string.analytics_event_welcome_find_out_more);
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

  public void configureFindMoreEspInfo() {
    analytics.trackAction(R.string.analytics_event_configure,
        R.string.analytics_event_configure_find_out_more);
  }

  public void configureSaveSuccess() {
    analytics.trackAction(R.string.analytics_event_configuration,
        R.string.analytics_event_configuration_save_success);
  }

  public void configureStateConnected() {
    analytics.trackAction(R.string.analytics_event_configuration,
        R.string.analytics_event_configuration_state_connected);
  }

  public void configureStateDisconnected() {
    analytics.trackAction(R.string.analytics_event_configuration,
        R.string.analytics_event_configuration_state_disconnected);
  }

  public void configureCloseSuccess() {
    analytics.trackAction(R.string.analytics_event_configuration,
        R.string.analytics_event_configuration_close_success);
  }

  public void endBackPressed() {
    analytics.trackAction(R.string.analytics_event_end,
        R.string.analytics_event_end_back_pressed);
  }

  public void endConfigureAnother() {
    analytics.trackAction(R.string.analytics_event_end,
        R.string.analytics_event_end_configure_another);
  }

  public void endExit() {
    analytics.trackAction(R.string.analytics_event_end,
        R.string.analytics_event_end_exit);
  }

}
