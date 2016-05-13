package au.com.umranium.nodemcuwifi.presentation.task.connecting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.task.configuring.ConfiguringActivity;

/**
 * An activity that connects to a ESP8266 node.
 */
public class ConnectingActivity extends BaseTaskActivity implements ConnectingController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ConnectingActivity.class);
  }

  @NonNull
  @Override
  protected BaseTaskController createController() {
    return new ConnectingController(this);
  }

  @Override
  public void proceedToNextScreen() {
    startActivity(ConfiguringActivity.createIntent(this));
  }
}
