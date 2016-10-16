package au.com.umranium.espconnect.di.activity;

import android.app.Activity;
import android.content.Context;

import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.di.qualifiers.ActivityInstance;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

  private final BaseActivity activity;

  public ActivityModule(BaseActivity activity) {
    this.activity = activity;
  }

  @Provides
  @ActivityInstance
  public Context provideContext() {
    return activity;
  }

  @Provides
  public Activity provideActivity() {
    return activity;
  }

  @Provides
  public BaseActivity provideBaseActivity() {
    return activity;
  }

}
