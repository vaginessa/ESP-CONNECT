package au.com.umranium.espconnect.app.common;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import au.com.umranium.espconnect.di.scope.AppScope;

/**
 * Wraps and delegates to {@link WifiManager}.
 */
@AppScope
public class ConnectivityManagerSystemSurface {

  private final ConnectivityManager mConnectivityManager;

  public ConnectivityManagerSystemSurface(ConnectivityManager connectivityManager) {
    this.mConnectivityManager = connectivityManager;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public Network[] getAllNetworks() {
    return mConnectivityManager.getAllNetworks();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public NetworkInfo getNetworkInfo(Network network) {
    return mConnectivityManager.getNetworkInfo(network);
  }
}
