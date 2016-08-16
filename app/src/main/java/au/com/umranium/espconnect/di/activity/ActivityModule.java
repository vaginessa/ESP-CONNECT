package au.com.umranium.espconnect.di.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import au.com.umranium.espconnect.di.qualifiers.ActivityInstance;
import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.app.common.ToastDispatcher;
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

  @Provides
  public WifiManager provideWifiManager() {
    return (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
  }

  @Provides
  public ConnectivityManager provideConnectivityManager() {
    return (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  @Provides
  public ToastDispatcher provideToastDispatcher() {
    return new ToastDispatcher(activity);
  }

}
