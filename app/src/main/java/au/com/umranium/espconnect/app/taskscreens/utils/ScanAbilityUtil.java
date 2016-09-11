package au.com.umranium.espconnect.app.taskscreens.utils;

import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;

/**
 * Helps in ensuring wifi scanning is enabled.
 */
public class ScanAbilityUtil {

  private static final int AP_STATE_DISABLED = 11;

  private final WifiManager wifiManager;

  @Inject
  public ScanAbilityUtil(WifiManager wifiManager) {
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
    try {
      Method method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
      method.setAccessible(true);
      int actualState = (Integer) method.invoke(wifiManager, (Object[]) null);
      return actualState == AP_STATE_DISABLED;
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}
