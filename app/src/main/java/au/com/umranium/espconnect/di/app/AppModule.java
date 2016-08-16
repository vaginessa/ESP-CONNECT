package au.com.umranium.espconnect.di.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import au.com.umranium.espconnect.di.qualifiers.AppInstance;
import au.com.umranium.espconnect.di.scope.AppScope;
import au.com.umranium.espconnect.app.App;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
public class AppModule {

  private final App app;

  public AppModule(App app) {
    this.app = app;
  }

  @Provides
  @AppScope
  public App provideApp() {
    return app;
  }

  @Provides
  @AppScope
  @AppInstance
  public Context provideAppContext() {
    return app;
  }

  @Provides
  @AppScope
  @Named("EspSsidPattern")
  public String provideEspSsidPattern() {
    return "ESP.*";
  }

  @Provides
  public WifiManager provideWifiManager() {
    return (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
  }

  @Provides
  public ConnectivityManager provideConnectivityManager() {
    return (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

}
