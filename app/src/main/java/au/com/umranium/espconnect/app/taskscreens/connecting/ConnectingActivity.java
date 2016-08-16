package au.com.umranium.espconnect.app.taskscreens.connecting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.displayscreens.config.ConfigureActivity;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskActivity;
import au.com.umranium.espconnect.app.common.utils.IntentExtras;

import java.util.List;

/**
 * An activity that connects to a ESP8266 node.
 */
public class ConnectingActivity extends BaseTaskActivity<ConnectingController> implements ConnectingController.Surface {

  private static final String PARAM_ACCESS_POINT = "access_point";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull ScannedAccessPoint accessPoint) {
    Intent intent = new Intent(context, ConnectingActivity.class);
    intent.putExtra(PARAM_ACCESS_POINT, accessPoint);
    return intent;
  }

  private ScannedAccessPoint getAccessPointFromIntent() {
    return (ScannedAccessPoint) IntentExtras.getParcelableExtra(this, PARAM_ACCESS_POINT);
  }

  @Override
  protected void doInjection() {
    DaggerConnectingComponent.builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .connectingModule(new ConnectingModule(this, getAccessPointFromIntent()))
        .build()
        .inject(this);
  }

  @Override
  public void setTitle(String accessPointName) {
    super.setTitle(getString(R.string.connecting_title, accessPointName));
  }

  @Override
  public void setMessage(String accessPointName) {
    super.setMessage(getString(R.string.connecting_description, accessPointName));
  }

  @Override
  public void proceedToNextTask(List<ScannedAccessPoint> accessPoints) {
    Intent intent = ConfigureActivity.createIntent(this,
        getAccessPointFromIntent(), accessPoints);
    startNextActivity(intent);
  }


}
