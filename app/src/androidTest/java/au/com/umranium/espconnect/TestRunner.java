package au.com.umranium.espconnect;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import au.com.umranium.espconnect.app.TestApp;

/**
 * Substitutes the test application for the actual application in the instrumentation tests hence
 * allow mocking of injected classes.
 * <p>
 * Note: ensure to setup IDE to run tests using au.com.umranium.espconnect.TestRunner
 */
public class TestRunner extends AndroidJUnitRunner {

  @Override
  public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException,
    IllegalAccessException, ClassNotFoundException {
    String testApplicationClassName = TestApp.class.getCanonicalName();
    return super.newApplication(cl, testApplicationClassName, context);
  }

}

