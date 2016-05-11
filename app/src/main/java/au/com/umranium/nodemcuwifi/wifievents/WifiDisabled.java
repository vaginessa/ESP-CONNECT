package au.com.umranium.nodemcuwifi.wifievents;

/**
 * WiFi disabled event.
 *
 * @author umran
 */
public final class WifiDisabled extends WifiStateEvent {

  private static WifiDisabled sInstance;

  public static synchronized WifiDisabled getInstance() {
    if (sInstance == null) {
      sInstance = new WifiDisabled();
    }
    return sInstance;
  }

  private WifiDisabled() {
    // Do nothing
  }
}
