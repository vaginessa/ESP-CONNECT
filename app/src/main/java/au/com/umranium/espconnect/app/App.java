package au.com.umranium.espconnect.app;

import android.app.Application;

import com.google.android.gms.analytics.ExceptionReporter;

import au.com.umranium.espconnect.analytics.Analytics;
import au.com.umranium.espconnect.analytics.FullExceptionParser;
import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.app.AppModule;
import au.com.umranium.espconnect.di.app.DaggerAppComponent;

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

    Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    if (uncaughtExceptionHandler instanceof ExceptionReporter) {
      ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
      exceptionReporter.setExceptionParser(new FullExceptionParser());
    }
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }
}
