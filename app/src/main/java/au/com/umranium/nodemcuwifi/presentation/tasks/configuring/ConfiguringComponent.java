package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import au.com.umranium.nodemcuwifi.api.NodeMcuServiceModule;
import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.tasks.connecting.ConnectingActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ConfiguringModule.class, NodeMcuServiceModule.class})
public interface ConfiguringComponent {

  void inject(ConfiguringActivity activity);

}
