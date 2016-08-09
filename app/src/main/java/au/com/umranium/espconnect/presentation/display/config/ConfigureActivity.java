package au.com.umranium.espconnect.presentation.display.config;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.AccessPointArrayAdapter;
import au.com.umranium.espconnect.presentation.common.BaseActivity;
import au.com.umranium.espconnect.presentation.common.ConfigDetails;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.presentation.tasks.configuring.ConfiguringActivity;
import au.com.umranium.espconnect.presentation.utils.IntentExtras;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity that displays a list of ESP8266 access points for the user to pick one.
 */
public class ConfigureActivity extends BaseActivity<ConfigureController> implements ConfigureController.Surface {

  private static final String PARAM_ACCESS_POINT = "access_point";
  private static final String PARAM_SSIDS = "ssids";

  private RecyclerView list;
  private AccessPointArrayAdapter adapter;
  private EditText ssid;
  private EditText password;


  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull ScannedAccessPoint accessPoint,
                                    @NonNull List<ScannedAccessPoint> ssids) {
    Intent intent = new Intent(context, ConfigureActivity.class);
    intent.putExtra(PARAM_ACCESS_POINT, accessPoint);
    ScannedAccessPoint[] ssidArr = ssids.toArray(new ScannedAccessPoint[ssids.size()]);
    intent.putExtra(PARAM_SSIDS, ssidArr);
    return intent;
  }

  private ScannedAccessPoint getAccessPointFromIntent() {
    return (ScannedAccessPoint) IntentExtras.getParcelableExtra(this, PARAM_ACCESS_POINT);
  }

  private List<ScannedAccessPoint> getSsidsFromIntent() {
    Parcelable[] accessPointArr = IntentExtras.getParcelableArrayExtra(this, PARAM_SSIDS);
    ArrayList<ScannedAccessPoint> accessPoints = new ArrayList<>(accessPointArr.length);
    for (Parcelable parcelable : accessPointArr) {
      accessPoints.add((ScannedAccessPoint) parcelable);
    }
    return accessPoints;
  }

  @Override
  protected void doInjection() {
    DaggerConfigureComponent
        .builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .configureModule(new ConfigureModule(this, getAccessPointFromIntent(), getSsidsFromIntent()))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_configure);

    list = (RecyclerView) findViewById(R.id.ssid_list);
    assert list != null;
    list.setLayoutManager(new LinearLayoutManager(this));

    ssid = (EditText) findViewById(R.id.edt_ssid);
    password = (EditText) findViewById(R.id.edt_password);
    Button submit = (Button) findViewById(R.id.btn_submit);

    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        controller.onSubmit(ssid.getText().toString(), password.getText().toString());
      }
    });
  }

  @Override
  public void initListAdapter(Observer<ScannedAccessPoint> ssidClickObserver) {
    adapter = new AccessPointArrayAdapter(this, ssidClickObserver, AccessPointArrayAdapter.SORT_BY_SSID);
    list.setAdapter(adapter);
  }

  @Override
  public void showSsids(List<ScannedAccessPoint> ssids) {
    adapter.populate(ssids);
  }

  @Override
  public void updateInputSsid(String ssid) {
    this.ssid.setText(ssid);
  }

  @Override
  public void showSsidError(@StringRes int errorMsg) {
    ssid.setError(getString(errorMsg));
  }

  @Override
  public void showPasswordError(@StringRes int errorMsg) {
    password.setError(getString(errorMsg));
  }

  @Override
  public void clearErrors() {
    ssid.setError(null);
    password.setError(null);
  }

  @Override
  public void proceedToNextTask(ConfigDetails configDetails) {
    startNextActivity(ConfiguringActivity.createIntent(this, getAccessPointFromIntent(), configDetails));
  }
}