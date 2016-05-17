package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.configuring.ConfiguringActivity;
import au.com.umranium.nodemcuwifi.presentation.utils.IntentExtras;

/**
 * An activity that connects to a ESP8266 node.
 */
public class ConnectingActivity extends BaseTaskActivity implements ConnectingController.Surface {

  private static final String PARAM_ACCESS_POINT = "access_point";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull ScannedAccessPoint accessPoint) {
    Intent intent = new Intent(context, ConnectingActivity.class);
    intent.putExtra(PARAM_ACCESS_POINT, accessPoint);
    return intent;
  }

  @NonNull
  @Override
  protected BaseTaskController createController() {
    return new ConnectingController(this,
        (ScannedAccessPoint) IntentExtras.getParcelableExtra(this, PARAM_ACCESS_POINT));
  }

  @Override
  protected Intent createIntentForNextTask() {
    return ConfiguringActivity.createIntent(this);
  }

  @Override
  public void setTitle(String accessPointName) {
    super.setTitle(getString(R.string.connecting_title, accessPointName));
  }

  @Override
  public void setMessage(String accessPointName) {
    super.setMessage(getString(R.string.connecting_description, accessPointName));
  }

}
