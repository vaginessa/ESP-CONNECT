package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.presentation.display.end.EndActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;

/**
 * An activity that configures an ESP8266 node.
 */
public class ConfiguringActivity extends BaseTaskActivity implements ConfiguringController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ConfiguringActivity.class);
  }

  @NonNull
//  @Override
  protected BaseTaskController createController() {
    return new ConfiguringController(this);
  }

  @Override
  protected Intent createIntentForNextTask() {
    return EndActivity.createIntent(this);
  }

}
