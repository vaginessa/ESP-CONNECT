package au.com.umranium.nodemcuwifi.presentation.tasks.scanning;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.display.aplist.AccessPointListActivity;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorActivity;
import au.com.umranium.nodemcuwifi.presentation.display.welcome.DaggerWelcomeComponent;
import au.com.umranium.nodemcuwifi.presentation.display.welcome.WelcomeModule;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;

import java.util.List;

/**
 * An activity that scans for ESP8266 nodes.
 */
public class ScanningActivity extends BaseTaskActivity<ScanningController> implements ScanningController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ScanningActivity.class);
  }

  @Override
  protected void doInjection() {
    DaggerScanningComponent.builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .scanningModule(new ScanningModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected Intent createIntentForNextTask() {
    throw new RuntimeException();
  }

  @Override
  public void proceedWithAccessPoints(List<ScannedAccessPoint> accessPoints) {
    Intent intent = AccessPointListActivity.createIntent(this, accessPoints);
    startNextActivity(intent);
  }

  @Override
  public void proceedWithNoAccessPoints() {
    Intent intent = ErrorActivity.createIntent(this,
        R.string.scanning_error_noap_title,
        R.string.scanning_error_noap_description);
    startNextActivity(intent);
  }
}
