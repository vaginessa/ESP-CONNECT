package au.com.umranium.espconnect.app.taskscreens.configuring;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.displayscreens.end.EndActivity;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskActivity;
import au.com.umranium.espconnect.app.common.utils.IntentExtras;

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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setAnimation(R.drawable.scanning_animation);
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
