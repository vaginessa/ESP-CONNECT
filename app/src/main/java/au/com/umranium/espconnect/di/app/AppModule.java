package au.com.umranium.espconnect.di.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import javax.inject.Named;

import au.com.umranium.espconnect.app.App;
import au.com.umranium.espconnect.app.common.ConnectivityManagerSystemSurface;
import au.com.umranium.espconnect.app.common.WifiManagerSystemSurface;
import au.com.umranium.espconnect.di.qualifiers.AppInstance;
import au.com.umranium.espconnect.di.scope.AppScope;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

  private final App app;

  public AppModule(App app) {
    this.app = app;
  }

  @Provides
  @AppScope
  App provideApp() {
    return app;
  }

  @Provides
  @AppScope
  @AppInstance
  Context provideAppContext() {
    return app;
  }

  @Provides
  @AppScope
  @Named("EspSsidPattern")
  String provideEspSsidPattern() {
    return "ESP.*";
  }

  @Provides
  public WifiManagerSystemSurface provideWifiManager() {
    return new WifiManagerSystemSurface((WifiManager) app.getSystemService(Context.WIFI_SERVICE));
  }

  @Provides
  public ConnectivityManagerSystemSurface provideConnectivityManager() {
    return new ConnectivityManagerSystemSurface((ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE));
  }

}
