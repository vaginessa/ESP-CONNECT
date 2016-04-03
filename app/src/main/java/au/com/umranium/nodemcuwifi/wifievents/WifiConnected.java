package au.com.umranium.nodemcuwifi.wifievents;

import android.net.wifi.WifiInfo;
import android.support.annotation.NonNull;

/**
 * WiFi connected event.
 *
 * @author umran
 */
public class WifiConnected extends WifiConnectivityEvent {

    @NonNull
    private final WifiInfo mWifiInfo;

    public WifiConnected(@NonNull WifiInfo wifiInfo) {
        mWifiInfo = wifiInfo;
    }

    @NonNull
    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

}
