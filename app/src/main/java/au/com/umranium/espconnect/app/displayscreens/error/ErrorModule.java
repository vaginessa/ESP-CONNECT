package au.com.umranium.espconnect.app.displayscreens.error;

import javax.inject.Named;

import au.com.umranium.espconnect.di.scope.ActivityScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ErrorModule {

  private final ErrorActivity activity;
  private final String title;
  private final String description;

  public ErrorModule(ErrorActivity activity, String title, String description) {
    this.activity = activity;
    this.title = title;
    this.description = description;
  }

  @Provides
  @ActivityScope
  public ErrorController.Surface provideSurface() {
    return activity;
  }

  @Provides
  @ActivityScope
  @Named("title")
  public String provideTitle() {
    return title;
  }

  @Provides
  @ActivityScope
  @Named("description")
  public String provideDescription() {
    return description;
  }

}
