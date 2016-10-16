package au.com.umranium.espconnect.app;

import android.app.Application;

import com.google.android.gms.analytics.ExceptionReporter;

import javax.inject.Inject;

import au.com.umranium.espconnect.analytics.FullExceptionParser;
import au.com.umranium.espconnect.di.app.AppComponent;
import au.com.umranium.espconnect.di.app.AppModule;
import au.com.umranium.espconnect.di.app.DaggerAppComponent;

public class App extends Application {

  @Inject
  protected AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    setupDagger();
    setupAnalyticsExceptionReporter();
  }

  protected void setupDagger() {
    DaggerAppComponent
      .builder()
      .appModule(new AppModule(this))
      .build()
      .inject(this);
  }

  protected void setupAnalyticsExceptionReporter() {
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
