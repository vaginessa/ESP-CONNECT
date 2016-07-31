package au.com.umranium.espconnect.presentation.common;

import au.com.umranium.espconnect.presentation.tasks.utils.WifiConnectionUtil;
import rx.functions.Func1;

/**
 * Return true if the mobile device is tracking a WiFi network.
 */
public class IsTrackingWifiNetwork<T> implements Func1<T, Boolean> {

  private final WifiConnectionUtil wifiConnectionUtil;

  public IsTrackingWifiNetwork(WifiConnectionUtil wifiConnectionUtil) {
    this.wifiConnectionUtil = wifiConnectionUtil;
  }

  @Override
  public Boolean call(T ignored) {
    return wifiConnectionUtil.isTrackingWifiNetwork();
  }
}
