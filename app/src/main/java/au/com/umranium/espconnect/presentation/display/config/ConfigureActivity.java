package au.com.umranium.espconnect.presentation.display.config;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.BaseActivity;
import au.com.umranium.espconnect.presentation.common.ConfigDetails;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.presentation.tasks.configuring.ConfiguringActivity;
import au.com.umranium.espconnect.presentation.utils.IntentExtras;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The activity that displays a list of ESP8266 access points for the user to pick one.
 */
public class ConfigureActivity extends BaseActivity<ConfigureController> implements ConfigureController.Surface {

  private static final String PARAM_ACCESS_POINT = "access_point";
  private static final String PARAM_SSIDS = "ssids";

  private LinearLayout list;
  private Observer<ScannedAccessPoint> ssidClickObserver;
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

    list = (LinearLayout) findViewById(R.id.ssid_list);
    assert list != null;

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
    this.ssidClickObserver = ssidClickObserver;
  }

  @Override
  public void showSsids(List<ScannedAccessPoint> ssids) {
    list.removeAllViews();
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    for (int i = 0, l=ssids.size(); i < l; i++) {
      View item = layoutInflater.inflate(R.layout.layout_scanned_access_point, list, false);

      TextView name = (TextView) item.findViewById(R.id.txt_name);
      TextView sigStrength = (TextView) item.findViewById(R.id.txt_sig_strength);
      final ScannedAccessPoint accessPoint = ssids.get(i);

      item.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ssidClickObserver.onNext(accessPoint);
        }
      });

      name.setText(accessPoint.getSsid());
      sigStrength.setText(String.format(Locale.US, "%d%%", accessPoint.getSignalStrength()));

      list.addView(item);
    }
  }

  @Override
  public void updateInputSsid(String ssid) {
    this.ssid.setText(ssid);
    if (this.ssid.hasFocus()) {
      this.password.requestFocus();
    }
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
