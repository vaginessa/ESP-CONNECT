package au.com.umranium.espconnect.presentation.tasks.configuring;

import au.com.umranium.espconnect.api.NodeMcuServiceModule;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, ConfiguringModule.class, NodeMcuServiceModule.class})
public interface ConfiguringComponent {

  void inject(ConfiguringActivity activity);

}
