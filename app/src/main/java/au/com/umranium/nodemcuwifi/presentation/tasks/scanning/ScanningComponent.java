package au.com.umranium.nodemcuwifi.presentation.tasks.scanning;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ScanningModule.class})
public interface ScanningComponent {

  void inject(ScanningActivity activity);

}
