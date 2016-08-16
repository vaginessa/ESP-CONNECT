package au.com.umranium.espconnect.taskscreens.connecting;

import au.com.umranium.espconnect.api.NodeMcuServiceModule;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, ConnectingModule.class, NodeMcuServiceModule.class})
public interface ConnectingComponent {

  void inject(ConnectingActivity activity);

}
