package au.com.umranium.nodemcuwifi.presentation.display.end;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {EndModule.class})
public interface EndComponent {

  void inject(EndActivity activity);

}
