package au.com.umranium.nodemcuwifi.wifievents;

/**
 * WiFi disconnected event.
 *
 * @author umran
 */
public class WifiDisconnected extends WifiConnectivityEvent {

    private static WifiDisconnected sInstance;

    public static synchronized WifiDisconnected getInstance() {
        if (sInstance == null) {
            sInstance = new WifiDisconnected();
        }
        return sInstance;
    }

    private WifiDisconnected() {
        // Do nothing
    }

}
