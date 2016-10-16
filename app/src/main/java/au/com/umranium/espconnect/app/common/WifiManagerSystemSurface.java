package au.com.umranium.espconnect.app.common;

import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.CheckResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import au.com.umranium.espconnect.di.scope.AppScope;

/**
 * Wraps and delegates to {@link WifiManager}.
 */
@AppScope
public class WifiManagerSystemSurface {

  private final WifiManager mWifiManager;

  public WifiManagerSystemSurface(WifiManager mWifiManager) {
    this.mWifiManager = mWifiManager;
  }

  @CheckResult
  public String getCurrentlyConnectedSsid() {
    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
    if (wifiInfo == null) {
      return null;
    }
    return wifiInfo.getSSID();
  }

  @CheckResult
  public boolean disconnect() {
    return mWifiManager.disconnect();
  }

  @CheckResult
  public boolean enableNetwork(int netId, boolean disableOthers) {
    return mWifiManager.enableNetwork(netId, disableOthers);
  }

  @CheckResult
  public DhcpInfo getDhcpInfo() {
    return mWifiManager.getDhcpInfo();
  }

  @CheckResult
  public List<WifiConfiguration> getConfiguredNetworks() {
    return mWifiManager.getConfiguredNetworks();
  }

  @CheckResult
  public int addNetwork(WifiConfiguration config) {
    return mWifiManager.addNetwork(config);
  }

  @CheckResult
  public List<ScanResult> getScanResults() {
    return mWifiManager.getScanResults();
  }

  public boolean startScan() {
    return mWifiManager.startScan();
  }

  public boolean isWifiEnabled() {
    return mWifiManager.isWifiEnabled();
  }

  public boolean setWifiEnabled(boolean enabled) {
    return mWifiManager.setWifiEnabled(enabled);
  }

  public WifiInfo getConnectionInfo() {
    return mWifiManager.getConnectionInfo();
  }

  public int getWifiApState() {
    try {
      Method method = mWifiManager.getClass().getDeclaredMethod("getWifiApState");
      method.setAccessible(true);
      return (Integer) method.invoke(mWifiManager, (Object[]) null);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
