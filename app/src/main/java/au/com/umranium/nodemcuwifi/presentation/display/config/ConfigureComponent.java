package au.com.umranium.nodemcuwifi.presentation.display.config;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.display.aplist.AccessPointListActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ConfigureModule.class})
public interface ConfigureComponent {

  void inject(ConfigureActivity activity);

}
