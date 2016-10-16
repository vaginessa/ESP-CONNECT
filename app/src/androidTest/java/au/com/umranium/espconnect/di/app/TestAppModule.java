package au.com.umranium.espconnect.di.app;

import au.com.umranium.espconnect.app.App;
import au.com.umranium.espconnect.app.common.ConnectivityManagerSystemSurface;
import au.com.umranium.espconnect.app.common.WifiManagerSystemSurface;

public class TestAppModule extends AppModule {

  private WifiManagerSystemSurface wifiManager;
  private ConnectivityManagerSystemSurface connectivityManager;

  public TestAppModule(App app) {
    super(app);
  }

  public void reset() {
    wifiManager = super.provideWifiManager();
    connectivityManager = super.provideConnectivityManager();
  }

  @Override
  public WifiManagerSystemSurface provideWifiManager() {
    return wifiManager;
  }

  public void setWifiManager(WifiManagerSystemSurface wifiManager) {
    this.wifiManager = wifiManager;
  }

  @Override
  public ConnectivityManagerSystemSurface provideConnectivityManager() {
    return connectivityManager;
  }

  public void setConnectivityManager(ConnectivityManagerSystemSurface connectivityManager) {
    this.connectivityManager = connectivityManager;
  }
}
