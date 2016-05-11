package au.com.umranium.nodemcuwifi.wifievents;

/**
 * WiFi enabled event.
 *
 * @author umran
 */
public final class WifiEnabled extends WifiStateEvent {

  private static WifiEnabled sInstance;

  public static synchronized WifiEnabled getInstance() {
    if (sInstance == null) {
      sInstance = new WifiEnabled();
    }
    return sInstance;
  }

  private WifiEnabled() {
    // Do nothing
  }
}
