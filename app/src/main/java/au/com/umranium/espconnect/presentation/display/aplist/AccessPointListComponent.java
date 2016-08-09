package au.com.umranium.espconnect.presentation.display.aplist;

import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {AccessPointListModule.class})
public interface AccessPointListComponent {

  void inject(AccessPointListActivity activity);

}