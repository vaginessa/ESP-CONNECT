package au.com.umranium.nodemcuwifi.wifistatereceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import au.com.umranium.nodemcuwifi.wifievents.WifiConnectivityEvent;
import au.com.umranium.nodemcuwifi.wifievents.WifiDisabled;
import au.com.umranium.nodemcuwifi.wifievents.WifiEnabled;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import au.com.umranium.nodemcuwifi.wifievents.WifiScanComplete;

/**
 * @author umran
 */
public class WifiStateChangeReceiver extends BroadcastReceiver {

    private static final String TAG = WifiStateChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            if (wifiManager.isWifiEnabled()) {
                WifiEvents.getInstance().emitEvent(WifiEnabled.getInstance());
            } else {
                WifiEvents.getInstance().emitEvent(WifiDisabled.getInstance());
            }
        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            WifiEvents.getInstance().emitEvent(WifiConnectivityEvent.getInstance());
        } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            WifiEvents.getInstance().emitEvent(WifiScanComplete.getInstance());
        }
    }

}
