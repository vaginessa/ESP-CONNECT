package au.com.umranium.nodemcuwifi.di.activity;

import android.app.Activity;
import android.content.Context;
import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
public class ActivityModule {

  private BaseActivity activity;

  public ActivityModule(@ActivityScope BaseActivity activity) {
    this.activity = activity;
  }

  @Provides
  @Named("activity")
  public Context provideContext() {
    return activity;
  }

  @Provides
  @ActivityScope
  public Activity provideActivity() {
    return activity;
  }

}
