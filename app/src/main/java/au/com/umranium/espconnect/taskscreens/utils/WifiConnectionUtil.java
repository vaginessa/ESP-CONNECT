package au.com.umranium.espconnect.taskscreens.utils;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.CheckResult;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.common.ScannedAccessPoint;
import au.com.umranium.espconnect.rx.Pred;

/**
 * Utility class that helps connecting to a particular wifi access point.
 */
public class WifiConnectionUtil {

  private final WifiManager mWifiManager;
  private final ConnectivityManager mConnectivityManager;
  private final String mQuotedSsid;

  @Inject
  public WifiConnectionUtil(WifiManager wifiManager, ConnectivityManager connectivityManager,
                            ScannedAccessPoint scannedAccessPoint) {
    mWifiManager = wifiManager;
    mConnectivityManager = connectivityManager;
    mQuotedSsid = scannedAccessPoint.getQuotedSsid();
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

    Log.d("WifiConnectionUtil", " Connect to network: " + netId);

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

  @CheckResult
  public boolean isTrackingWifiNetwork() {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      for (Network network : mConnectivityManager.getAllNetworks()) {
        NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  @TargetApi(VERSION_CODES.LOLLIPOP)
  @CheckResult
  public Network getWifiNetwork() {
    List<Network> networks = new ArrayList<>();
    for (Network network : mConnectivityManager.getAllNetworks()) {
      NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
      if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        networks.add(network);
      }
    }
    if (networks.size() == 1) {
      return networks.get(0);
    } else if (networks.size() == 0) {
      return null;
    } else {
      Log.wtf(WifiConnectionUtil.class.getSimpleName(), "Found multiple WiFi network objects found!");
      return networks.get(0);
    }
  }

  @CheckResult
  private WifiConfiguration getConfiguredNetwork() {
    List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
    if (configuredNetworks == null) {
      return null;
    }
    for (WifiConfiguration conf : configuredNetworks) {
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

  /**
   * Returns true if the mobile device is connected to the ESP's hotspot.
   */
  public class IsConnectedToEsp<T> extends Pred<T> {
    @Override
    public Boolean call(T t) {
      return isAlreadyConnected();
    }
  }

  /**
   * Return true if the mobile device is tracking a WiFi network.
   */
  public class IsTrackingWifiNetwork<T> extends Pred<T> {
    @Override
    public Boolean call(T ignored) {
      return isTrackingWifiNetwork();
    }
  }
}
