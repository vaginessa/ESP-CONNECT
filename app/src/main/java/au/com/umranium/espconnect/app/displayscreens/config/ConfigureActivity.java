package au.com.umranium.espconnect.app.displayscreens.config;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.common.utils.IntentExtras;
import au.com.umranium.espconnect.app.taskscreens.configuring.ConfiguringActivity;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import rx.Observer;

/**
 * The activity that displays a list of ESP8266 access points for the user to pick one.
 */
public class ConfigureActivity extends BaseActivity<ConfigureController> implements ConfigureController.Surface {

  private static final String PARAM_ACCESS_POINT = "access_point";
  private static final String PARAM_SSIDS = "ssids";

  private ScrollView scrollContainer;
  private LinearLayout list;
  private Observer<ScannedAccessPoint> ssidClickObserver;
  private EditText edtSsid;
  private TextInputLayout layoutPassword;
  private EditText edtPassword;
  private CheckBox chkPassword;


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

    scrollContainer = (ScrollView) findViewById(R.id.scroll_container);

    list = (LinearLayout) findViewById(R.id.ssid_list);
    assert list != null;

    edtSsid = (EditText) findViewById(R.id.edt_ssid);
    edtPassword = (EditText) findViewById(R.id.edt_password);
    TextView submit = (TextView) findViewById(R.id.btn_submit);

    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        controller.onSubmit(edtSsid.getText().toString(), edtPassword.getText().toString(), chkPassword.isChecked());
      }
    });

    TextView moreInfo = (TextView) findViewById(R.id.txt_link_to_more_info_about_esp);
    assert moreInfo != null;
    moreInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        controller.onMoreEspInfoTxtClicked();
      }
    });

    layoutPassword = (TextInputLayout) findViewById(R.id.layout_txt_password);
    assert layoutPassword != null;

    chkPassword = (CheckBox) findViewById(R.id.chk_password);
    assert chkPassword != null;
    chkPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        updatePasswordEnabledState();
      }
    });
    updatePasswordEnabledState();

    scrollContainer.requestFocus();
  }

  private void updatePasswordEnabledState() {
    boolean checked = chkPassword.isChecked();
    layoutPassword.setEnabled(checked);
    if (checked) {
      layoutPassword.setHint(getString(R.string.configure_hint_password));
    } else {
      layoutPassword.setHint(getString(R.string.configure_hint_no_password));
    }
  }

  @Override
  public void initListAdapter(Observer<ScannedAccessPoint> ssidClickObserver) {
    this.ssidClickObserver = ssidClickObserver;
  }

  @Override
  public void showSsids(List<ScannedAccessPoint> ssids) {
    list.removeAllViews();
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    for (int i = 0, l = ssids.size(); i < l; i++) {
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
    this.edtSsid.setText(ssid);
    if (this.edtSsid.hasFocus()) {
      this.edtPassword.requestFocus();
    }
  }

  @Override
  public void showSsidError(@StringRes int errorMsg) {
    edtSsid.setError(getString(errorMsg));
  }

  @Override
  public void showPasswordError(@StringRes int errorMsg) {
    edtPassword.setError(getString(errorMsg));
  }

  @Override
  public void clearErrors() {
    edtSsid.setError(null);
    edtPassword.setError(null);
  }

  @Override
  public void proceedToNextTask(ConfigDetails configDetails) {
    startNextActivity(ConfiguringActivity.createIntent(this, getAccessPointFromIntent(), configDetails));
  }
}
