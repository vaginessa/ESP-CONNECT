package au.com.umranium.nodemcuwifi.wifievents;

import android.net.wifi.WifiInfo;

/**
 * WiFi connected event.
 *
 * @author umran
 */
public class WifiConnected extends WifiConnectivityEvent {

    private final WifiInfo mWifiInfo;

    public WifiConnected(WifiInfo wifiInfo) {
        mWifiInfo = wifiInfo;
    }

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

}
