package au.com.umranium.espconnect.di.app;

import android.content.Context;
import au.com.umranium.espconnect.di.scope.AppScope;
import au.com.umranium.espconnect.presentation.app.App;
import au.com.umranium.espconnect.presentation.common.Scheduler;
import au.com.umranium.espconnect.wifievents.WifiEvents;
import dagger.Component;

import javax.inject.Named;

@AppScope
@Component(modules = {AppModule.class})
public interface AppComponent {

  void inject(App app);

  App getApp();

  @Named("app")
  Context getAppContext();

  WifiEvents getWifiEvents();

  Scheduler getScheduler();

}
