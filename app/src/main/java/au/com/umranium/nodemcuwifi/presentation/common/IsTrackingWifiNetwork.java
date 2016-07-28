package au.com.umranium.nodemcuwifi.presentation.common;

import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
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
