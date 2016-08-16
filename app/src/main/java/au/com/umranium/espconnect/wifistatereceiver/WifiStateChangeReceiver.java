package au.com.umranium.espconnect.wifistatereceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import au.com.umranium.espconnect.app.App;
import au.com.umranium.espconnect.wifievents.*;

/**
 * @author umran
 */
public class WifiStateChangeReceiver extends BroadcastReceiver {

  private static final String TAG = WifiStateChangeReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    App app = (App) context.getApplicationContext();
    WifiEvents wifiEvents = app.getAppComponent().getWifiEvents();

    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
      if (wifiManager.isWifiEnabled()) {
        wifiEvents.emitEvent(WifiEnabled.getInstance());
      } else {
        wifiEvents.emitEvent(WifiDisabled.getInstance());
      }
    } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
      NetworkInfo ntwkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      if (ntwkInfo.isConnected()) {
        WifiInfo wifiInfo;
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
          wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        } else {
          wifiInfo = wifiManager.getConnectionInfo();
        }
        wifiEvents.emitEvent(new WifiConnected(wifiInfo));
      } else {
        wifiEvents.emitEvent(WifiDisconnected.getInstance());
      }
    } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

      wifiEvents.emitEvent(WifiScanComplete.getInstance());
    }
  }

}
