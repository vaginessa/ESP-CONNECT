package au.com.umranium.espconnect.app.taskscreens.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;

import au.com.umranium.espconnect.app.common.WifiManagerSystemSurface;

/**
 * Helps in ensuring wifi scanning is enabled.
 */
public class ScanAbilityUtil {

  private static final int AP_STATE_DISABLED = 11;

  private final WifiManagerSystemSurface wifiManager;

  @Inject
  public ScanAbilityUtil(WifiManagerSystemSurface wifiManager) {
    this.wifiManager = wifiManager;
  }

  public boolean isWifiOn() {
    return wifiManager.isWifiEnabled();
  }


  public boolean turnWifiOn() {
    return wifiManager.setWifiEnabled(true);
  }

  @SuppressWarnings("TryWithIdenticalCatches")
  public boolean isAccessPointOff() {
    return wifiManager.getWifiApState() == AP_STATE_DISABLED;
  }

}
