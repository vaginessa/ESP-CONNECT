package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.display.config.ConfigureActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.scanning.DaggerScanningComponent;
import au.com.umranium.nodemcuwifi.presentation.tasks.scanning.ScanningModule;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.presentation.utils.IntentExtras;

import java.util.Arrays;
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
