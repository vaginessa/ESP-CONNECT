package au.com.umranium.nodemcuwifi.presentation.task.scanning;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.task.connecting.ConnectingActivity;

/**
 * An activity that scans for ESP8266 nodes.
 */
public class ScanningActivity extends BaseTaskActivity implements ScanningController.Surface {

  @Override
  @NonNull
  protected BaseTaskController createController() {
    return new ScanningController(this);
  }

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ScanningActivity.class);
  }

  @Override
  public void proceedToNextScreen() {
    startActivity(ConnectingActivity.createIntent(this));
  }
}
