package au.com.umranium.nodemcuwifi.presentation.display.aplist;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.display.end.EndActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {AccessPointListModule.class})
public interface AccessPointListComponent {

  void inject(AccessPointListActivity activity);

}
