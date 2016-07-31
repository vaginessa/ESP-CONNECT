package au.com.umranium.espconnect.presentation.display.welcome;

import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {WelcomeModule.class})
public interface WelcomeComponent {

  void inject(WelcomeActivity activity);

}
