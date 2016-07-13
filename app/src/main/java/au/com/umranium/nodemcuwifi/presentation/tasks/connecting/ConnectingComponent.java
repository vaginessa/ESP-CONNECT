package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.tasks.scanning.ScanningActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ConnectingModule.class})
public interface ConnectingComponent {

  void inject(ConnectingActivity activity);

}
