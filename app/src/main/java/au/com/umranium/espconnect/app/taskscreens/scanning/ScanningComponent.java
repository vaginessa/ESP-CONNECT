package au.com.umranium.espconnect.app.taskscreens.scanning;

import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, ScanningModule.class})
public interface ScanningComponent {

  void inject(ScanningActivity activity);

}
