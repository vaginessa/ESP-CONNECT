package au.com.umranium.nodemcuwifi.presentation.display.error;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.display.welcome.WelcomeActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {ErrorModule.class})
public interface ErrorComponent {

  void inject(ErrorActivity activity);

}
