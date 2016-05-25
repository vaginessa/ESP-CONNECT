package au.com.umranium.nodemcuwifi.presentation.display.welcome;

import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {WelcomeModule.class})
public interface WelcomeComponent {

  void inject(WelcomeActivity activity);

}
