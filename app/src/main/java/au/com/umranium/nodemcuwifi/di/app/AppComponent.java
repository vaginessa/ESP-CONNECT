package au.com.umranium.nodemcuwifi.di.app;

import android.content.Context;
import au.com.umranium.nodemcuwifi.di.scope.AppScope;
import au.com.umranium.nodemcuwifi.presentation.app.App;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
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

}
