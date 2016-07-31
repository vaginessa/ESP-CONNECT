package au.com.umranium.espconnect.presentation.tasks.scanning;

import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ScanningModule.class})
public interface ScanningComponent {

  void inject(ScanningActivity activity);

}
