package au.com.umranium.espconnect.di.app;

import android.content.Context;

import javax.inject.Named;

import au.com.umranium.espconnect.analytics.Analytics;
import au.com.umranium.espconnect.app.App;
import au.com.umranium.espconnect.app.common.ConnectivityManagerSystemSurface;
import au.com.umranium.espconnect.app.common.StringProvider;
import au.com.umranium.espconnect.app.common.WifiManagerSystemSurface;
import au.com.umranium.espconnect.di.qualifiers.AppInstance;
import au.com.umranium.espconnect.di.scope.AppScope;
import au.com.umranium.espconnect.rx.Scheduler;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import dagger.Component;

@AppScope
@Component(modules = {AppModule.class})
public interface AppComponent {

  void inject(App app);

  App getApp();

  @AppInstance
  Context getAppContext();

  WifiEvents getWifiEvents();

  Scheduler getScheduler();

  Analytics getAnalytics();

  WifiManagerSystemSurface getWifiManager();

  ConnectivityManagerSystemSurface getConnectivityManager();

  @Named("EspSsidPattern")
  String getEspSsidPattern();

  StringProvider getStringProvider();

}
