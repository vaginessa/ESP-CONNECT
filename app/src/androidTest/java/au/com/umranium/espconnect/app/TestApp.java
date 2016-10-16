package au.com.umranium.espconnect.app;

import au.com.umranium.espconnect.di.app.DaggerAppComponent;
import au.com.umranium.espconnect.di.app.TestAppModule;

public class TestApp extends App {

  private TestAppModule appModule = new TestAppModule(this);

  public TestAppModule getAppModule() {
    return appModule;
  }

  @Override
  protected void setupDagger() {
    DaggerAppComponent
      .builder()
      .appModule(appModule)
      .build()
      .inject(this);
  }

  @Override
  protected void setupAnalyticsExceptionReporter() {
    // Do nothing
  }
}
