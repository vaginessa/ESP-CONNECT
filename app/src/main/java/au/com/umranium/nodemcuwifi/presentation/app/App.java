package au.com.umranium.nodemcuwifi.presentation.app;

import android.app.Application;
import au.com.umranium.nodemcuwifi.di.app.AppComponent;
import au.com.umranium.nodemcuwifi.di.app.AppModule;
import au.com.umranium.nodemcuwifi.di.app.DaggerAppComponent;

import javax.inject.Inject;

public class App extends Application {

  @Inject
  protected AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    DaggerAppComponent
        .builder()
        .appModule(new AppModule(this))
        .build()
        .inject(this);
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }
}
