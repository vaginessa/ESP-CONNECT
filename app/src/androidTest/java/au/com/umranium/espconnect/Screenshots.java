package au.com.umranium.espconnect;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import au.com.umranium.espconnect.app.TestApp;
import au.com.umranium.espconnect.app.common.WifiManagerSystemSurface;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.displayscreens.aplist.AccessPointListActivity;
import au.com.umranium.espconnect.app.displayscreens.config.ConfigureActivity;
import au.com.umranium.espconnect.app.displayscreens.end.EndActivity;
import au.com.umranium.espconnect.app.displayscreens.welcome.WelcomeActivity;
import au.com.umranium.espconnect.app.taskscreens.configuring.ConfiguringActivity;
import au.com.umranium.espconnect.app.taskscreens.connecting.ConnectingActivity;
import au.com.umranium.espconnect.app.taskscreens.scanning.ScanningActivity;
import au.com.umranium.espconnect.di.app.TestAppModule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@SuppressWarnings("TryFinallyCanBeTryWithResources")
@RunWith(AndroidJUnit4.class)
public class Screenshots {

  @Rule
  public ActivityTestRule<WelcomeActivity> welcomeRule = new ActivityTestRule<>(WelcomeActivity.class, false, false);

  @Rule
  public ActivityTestRule<ScanningActivity> scanningRule = new ActivityTestRule<>(ScanningActivity.class, false, false);

  @Rule
  public ActivityTestRule<AccessPointListActivity> accessPointListRule = new ActivityTestRule<>(AccessPointListActivity.class, false, false);

  @Rule
  public ActivityTestRule<ConnectingActivity> connectingRule = new ActivityTestRule<>(ConnectingActivity.class, false, false);

  @Rule
  public ActivityTestRule<ConfigureActivity> configureRule = new ActivityTestRule<>(ConfigureActivity.class, false, false);

  @Rule
  public ActivityTestRule<ConfiguringActivity> configuringRule = new ActivityTestRule<>(ConfiguringActivity.class, false, false);

  @Rule
  public ActivityTestRule<EndActivity> endRule = new ActivityTestRule<>(EndActivity.class, false, false);

  private Instrumentation instrumentation;
  private TestApp app;
  private TestAppModule appModule;

  @BeforeClass
  public static void setUpClass() throws Exception {
    clearCacheDir(InstrumentationRegistry.getInstrumentation().getTargetContext());
  }

  @Before
  public void setUp() throws Exception {
    instrumentation = InstrumentationRegistry.getInstrumentation();
    app = (TestApp) instrumentation.getTargetContext().getApplicationContext();
    appModule = app.getAppModule();
    appModule.reset();
  }

  @Test
  public void welcomeScreen() {
    WelcomeActivity welcome = welcomeRule.launchActivity(null);
    takeScreenshot("1-welcome-screen", welcome);
  }

  @Test
  public void scanningScreen() {
    ScanningActivity scanning = scanningRule.launchActivity(
      ScanningActivity.createIntent(instrumentation.getTargetContext()));
    takeScreenshot("2-scanning-screen", scanning);
  }

  @Test
  public void accessPointListScreen() {
    AccessPointListActivity accessPointList = accessPointListRule.launchActivity(
      AccessPointListActivity.createIntent(instrumentation.getTargetContext(),
        Arrays.asList(
          new ScannedAccessPoint(0L, "ESP82991", 30),
          new ScannedAccessPoint(1L, "ESP82992", 20),
          new ScannedAccessPoint(2L, "ESP82993", 10)
        )));
    takeScreenshot("3-access-point-list-screen", accessPointList);
  }

  @Test
  public void connectingScreen() {
    mockConnectedWifi("ESP82991");

    ConnectingActivity connecting = connectingRule.launchActivity(
      ConnectingActivity.createIntent(instrumentation.getTargetContext(),
        new ScannedAccessPoint(0L, "ESP82991", 30)
      ));
    takeScreenshot("4-connecting-screen", connecting);
  }

  @Test
  public void configureScreen() {
    mockConnectedWifi("ESP82991");

    ConfigureActivity configureActivity = configureRule.launchActivity(
      ConfigureActivity.createIntent(instrumentation.getTargetContext(),
        new ScannedAccessPoint(0L, "ESP82991", 30),
        Arrays.asList(
          new ScannedAccessPoint(0L, "Home", 30),
          new ScannedAccessPoint(1L, "Away", 20),
          new ScannedAccessPoint(2L, "Neighbours", 10)
        )));
    onView(withId(R.id.edt_ssid))
      .perform(ViewActions.typeText("Home"));
    onView(withId(R.id.edt_password))
      .perform(ViewActions.typeText("password"));
    takeScreenshot("5-configure-screen", configureActivity);
  }

  @Test
  public void configuringScreen() {
    mockConnectedWifi("ESP82991");

    ConfiguringActivity connecting = configuringRule.launchActivity(
      ConfiguringActivity.createIntent(instrumentation.getTargetContext(),
        new ScannedAccessPoint(0L, "ESP82991", 30),
        new ConfigDetails("Home", "password")
      ));
    takeScreenshot("6-configuring-screen", connecting);
  }

  @Test
  public void endScreen() {
    EndActivity endActivity = endRule.launchActivity(
      EndActivity.createIntent(instrumentation.getTargetContext(), "Home"));
    takeScreenshot("7-end-screen", endActivity);
  }

  private void mockConnectedWifi(final String ssid) {
    WifiManager manager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
    WifiManagerSystemSurface systemSurface = new WifiManagerSystemSurface(manager) {
      @Override
      public String getCurrentlyConnectedSsid() {
        return "\"" + ssid + "\"";
      }
    };
    appModule.setWifiManager(systemSurface);
  }

  private static void clearCacheDir(Context context) {
    File[] pngFiles = context.getCacheDir().listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(".png");
      }
    });
    if (pngFiles == null || pngFiles.length == 0) {
      return;
    }
    for (File pngFile : pngFiles) {
      if (!pngFile.delete()) {
        throw new RuntimeException("Unable to delete existing PNG file: " + pngFile);
      }
    }
  }

  public static void takeScreenshot(String name, Activity activity) {
    File imageFile = new File(activity.getCacheDir(), name + ".png");

    View scrView = activity.getWindow().getDecorView().getRootView();
    scrView.setDrawingCacheEnabled(true);
    Bitmap bitmap = Bitmap.createBitmap(scrView.getDrawingCache());
    scrView.setDrawingCacheEnabled(false);
    try {
      OutputStream out = new FileOutputStream(imageFile);
      try {
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.flush();
      } finally {
        //noinspection ThrowFromFinallyBlock
        out.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
