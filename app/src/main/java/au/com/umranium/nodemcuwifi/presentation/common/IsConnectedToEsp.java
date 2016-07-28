package au.com.umranium.nodemcuwifi.presentation.common;

import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.utils.rx.Pred;

/**
 * Returns true if the mobile device is connected to the ESP's hotspot.
 */
public class IsConnectedToEsp<T> extends Pred<T> {

  private WifiConnectionUtil wifiConnectionUtil;

  public IsConnectedToEsp(WifiConnectionUtil wifiConnectionUtil) {
    this.wifiConnectionUtil = wifiConnectionUtil;
  }

  @Override
  public Boolean call(T t) {
    return wifiConnectionUtil.isAlreadyConnected();
  }
}
