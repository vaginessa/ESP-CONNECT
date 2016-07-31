package au.com.umranium.espconnect.presentation.display.end;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.TextView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.BaseActivity;

/**
 * The activity that displays the end message.
 */
public class EndActivity extends BaseActivity<EndController> implements EndController.Surface {

  private static final String PARAM_SSID = "ssid";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull String ssid) {
    Intent intent = new Intent(context, EndActivity.class);
    intent.putExtra(PARAM_SSID, ssid);
    return intent;
  }

  private TextView mTxtDescription;

  @Override
  protected void doInjection() {
    DaggerEndComponent
        .builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .endModule(new EndModule(this, getSsid()))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_end);

    mTxtDescription = (TextView) findViewById(R.id.txt_description);

    setResult(RESULT_OK);
  }

  @Override
  public void setDescription(String ssid) {
    mTxtDescription.setText(getString(R.string.end_txt_description, ssid));
  }

  private String getSsid() {
    return getIntent().getStringExtra(PARAM_SSID);
  }
}
