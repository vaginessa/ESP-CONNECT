package au.com.umranium.nodemcuwifi.configurer;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION_CODES;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.util.Log;
import au.com.umranium.nodemcuwifi.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author umran
 */
public class WifiController {

  private static final String TAG = WifiController.class.getSimpleName();

  private final WifiManager mWifiManager;
  private final ConnectivityManager mConnectivityManager;
  private final String mQuotedSsid;

  public WifiController(WifiManager wifiManager, ConnectivityManager connectivityManager,
                        String quotedSsid) {
    mWifiManager = wifiManager;
    mConnectivityManager = connectivityManager;
    mQuotedSsid = quotedSsid;
  }

  @CheckResult
  public boolean isAlreadyConnected() {
    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
    return wifiInfo != null && mQuotedSsid.equals(wifiInfo.getSSID());
  }

  public void connectToNetwork() throws WifiConnectionException {
    // TODO: Check and turn on wifi if necessary
    // TODO: Check and turn off hotspot if necessary

    WifiConfiguration wifiConf = getConfiguredNetwork();

    int netId;
    if (wifiConf != null) {
      // Network already added
      netId = wifiConf.networkId;
    } else {
      netId = addNetwork();
      if (netId < 0) {
        // add network failed
        throw new WifiConnectionException(
            R.string.wificonn_error_msg_unable_to_add_network);
      }
    }

    Log.d(TAG, " Connect to network: " + netId);

    if (!mWifiManager.disconnect()) {
      throw new WifiConnectionException(R.string.wificonn_error_msg_unable_to_disconnect);
    }
    if (!mWifiManager.enableNetwork(netId, true)) {
      throw new WifiConnectionException(R.string.wificonn_error_msg_unable_to_enable);
    }
  }

  @CheckResult
  public InetAddress getGateway() {
    DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
    int gateway = dhcpInfo.gateway;
    byte[] addr = new byte[4];
    for (int i = 0; i < addr.length; i++) {
      int val = (gateway >> i * 8) & 0xFF;
      addr[i] = (byte) val;
    }
    try {
      return Inet4Address.getByAddress(addr);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  @TargetApi(VERSION_CODES.LOLLIPOP)
  @NonNull
  @CheckResult
  public Network getWifiNetwork() {
    for (Network network : mConnectivityManager.getAllNetworks()) {
      NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
      if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        return network;
      }
    }
    // this shouldn't happen
    throw new RuntimeException("Unable to find WiFi network object");
  }

  @CheckResult
  private WifiConfiguration getConfiguredNetwork() {
    for (WifiConfiguration conf : mWifiManager.getConfiguredNetworks()) {
      if (conf.SSID != null && mQuotedSsid.equals(conf.SSID)) {
        return conf;
      }
    }
    return null;
  }

  @CheckResult
  private int addNetwork() {
    WifiConfiguration wifiConf = new WifiConfiguration();
    wifiConf.SSID = mQuotedSsid;
    wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    return mWifiManager.addNetwork(wifiConf);
  }

}
