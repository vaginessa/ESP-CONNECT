package au.com.umranium.nodemcuwifi.di.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import javax.inject.Named;

import au.com.umranium.nodemcuwifi.di.scope.ActivityScope;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import dagger.Module;
import dagger.Provides;

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
  public Activity provideActivity() {
    return activity;
  }

  @Provides
  public BaseActivity provideBaseActivity() {
    return activity;
  }

  @Provides
  public WifiManager provideWifiManager() {
    return (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
  }

  @Provides
  public ConnectivityManager provideConnectivityManager() {
    return (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

}
