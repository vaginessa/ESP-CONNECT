package au.com.umranium.nodemcuwifi.presentation.task.scanning;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.task.connecting.ConnectingActivity;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;

/**
 * An activity that scans for ESP8266 nodes.
 */
public class ScanningActivity extends BaseTaskActivity implements ScanningController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ScanningActivity.class);
  }

  @Override
  @NonNull
  protected BaseTaskController createController() {
    return new ScanningController(this,
        WifiEvents.getInstance(),
        (WifiManager) getSystemService(WIFI_SERVICE));
  }

  @Override
  protected Intent createIntentForNextTask() {
    return ConnectingActivity.createIntent(this);
  }
}
