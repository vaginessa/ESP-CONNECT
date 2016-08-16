package au.com.umranium.espconnect.di.app;

import android.content.Context;

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
  @AppInstance
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

  @Provides
  @AppScope
  @Named("EspSsidPattern")
  public String provideEspSsidPattern() {
    return "ESP.*";
  }
}
