package au.com.umranium.espconnect.wifievents;

/**
 * A WiFi scan completed.
 *
 * @author umran
 */
public final class WifiScanComplete extends WifiEvent {

  private static WifiScanComplete sInstance;

  public static synchronized WifiScanComplete getInstance() {
    if (sInstance == null) {
      sInstance = new WifiScanComplete();
    }
    return sInstance;
  }

  private WifiScanComplete() {
    // Do nothing
  }

}
