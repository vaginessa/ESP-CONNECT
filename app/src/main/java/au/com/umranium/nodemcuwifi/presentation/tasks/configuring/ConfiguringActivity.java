package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.common.ConfigDetails;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.display.end.EndActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.utils.IntentExtras;

/**
 * An activity that configures an ESP8266 node.
 */
public class ConfiguringActivity extends BaseTaskActivity<ConfiguringController> implements ConfiguringController.Surface {

  private static final String PARAM_ACCESS_POINT = "access_point";
  private static final String PARAM_CONFIG_DETAILS = "config_details";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull ScannedAccessPoint accessPoint,
                                    @NonNull ConfigDetails configDetails) {
    Intent intent = new Intent(context, ConfiguringActivity.class);
    intent.putExtra(PARAM_ACCESS_POINT, accessPoint);
    intent.putExtra(PARAM_CONFIG_DETAILS, configDetails);
    return intent;
  }

  private ScannedAccessPoint getAccessPointFromIntent() {
    return (ScannedAccessPoint) IntentExtras.getParcelableExtra(this, PARAM_ACCESS_POINT);
  }

  private ConfigDetails getConfigDetailsFromIntent() {
    return IntentExtras.getParcelableExtra(this, PARAM_CONFIG_DETAILS);
  }

  @Override
  protected void doInjection() {
    DaggerConfiguringComponent.builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .configuringModule(new ConfiguringModule(this, getAccessPointFromIntent(), getConfigDetailsFromIntent()))
        .build()
        .inject(this);
  }

  @Override
  public void setMessage(String networkName) {
    super.setMessage(getString(R.string.configuring_description, networkName));
  }

  @Override
  public void proceedToNextTask(String ssid) {
    startNextActivity(EndActivity.createIntent(this, ssid));
  }

}
