package au.com.umranium.nodemcuwifi.di.app;

import android.content.Context;
import android.net.wifi.WifiManager;

import au.com.umranium.nodemcuwifi.di.scope.AppScope;
import au.com.umranium.nodemcuwifi.presentation.app.App;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
public class AppModule {

  private App app;

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
  @Named("app")
  public Context provideAppContext() {
    return app;
  }

  @Provides
  @AppScope
  public WifiEvents provideWifiEvents() {
    return new WifiEvents();
  }

  @Provides
  @AppScope
  public Scheduler provideScheduler() {
    return new Scheduler();
  }

}
