package au.com.umranium.espconnect.presentation.display.config;

import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ConfigureModule.class})
public interface ConfigureComponent {

  void inject(ConfigureActivity activity);

}
