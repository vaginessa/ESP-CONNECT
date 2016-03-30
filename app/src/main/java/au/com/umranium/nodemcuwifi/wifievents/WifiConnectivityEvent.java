package au.com.umranium.nodemcuwifi.wifievents;

/**
 * A WiFi connectivity event.
 *
 * @author umran
 */
public class WifiConnectivityEvent extends WifiEvent {


    private static WifiConnectivityEvent sInstance;

    public static synchronized WifiConnectivityEvent getInstance() {
        if (sInstance == null) {
            sInstance = new WifiConnectivityEvent();
        }
        return sInstance;
    }

    private WifiConnectivityEvent() {
        // Do nothing
    }

}
