package au.com.umranium.espconnect.app.displayscreens.config;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, ConfigureModule.class})
public interface ConfigureComponent {

  void inject(ConfigureActivity activity);

}
