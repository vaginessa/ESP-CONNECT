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

  private final BaseActivity activity;

  public ActivityModule(BaseActivity activity) {
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


  @Provides
  @ActivityScope
  public BaseActivity provideBaseActivity() {
    return activity;
  }

}
