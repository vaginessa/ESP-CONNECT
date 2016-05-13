package au.com.umranium.nodemcuwifi.presentation.task.configuring;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.presentation.end.EndActivity;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.task.common.BaseTaskController;

/**
 * An activity that configures an ESP8266 node.
 */
public class ConfiguringActivity extends BaseTaskActivity implements ConfiguringController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ConfiguringActivity.class);
  }

  @NonNull
  @Override
  protected BaseTaskController createController() {
    return new ConfiguringController(this);
  }

  @Override
  public void proceedToNextScreen() {
    startActivity(EndActivity.createIntent(this));
  }

}
