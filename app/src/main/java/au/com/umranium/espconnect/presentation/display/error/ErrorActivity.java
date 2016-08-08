package au.com.umranium.espconnect.presentation.display.error;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.BaseActivity;
import au.com.umranium.espconnect.presentation.utils.IntentExtras;

/**
 * An generic activity that displays errors.
 */
public class ErrorActivity extends BaseActivity<ErrorController> implements ErrorController.Surface {

  private static final String PARAM_TITLE = "title";
  private static final String PARAM_DESCRIPTION = "description";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull String title,
                                    @NonNull String description) {
    Intent intent = new Intent(context, ErrorActivity.class);
    intent.putExtra(PARAM_TITLE, title);
    intent.putExtra(PARAM_DESCRIPTION, description);
    return intent;
  }

  @Override
  protected void doInjection() {
    DaggerErrorComponent
        .builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .errorModule(new ErrorModule(this, getParamTitle(), getParamDescription()))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_error);
  }

  @Override
  public void showScreen(String title, String description) {
    TextView txtTitle = (TextView) findViewById(R.id.txt_title);
    TextView txtDescription = (TextView) findViewById(R.id.txt_description);
    Button btnOk = (Button) findViewById(R.id.btn_ok);

    assert txtTitle != null;
    assert txtDescription != null;
    assert btnOk != null;

    txtTitle.setText(title);
    txtDescription.setText(description);
    btnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        controller.onOkBtnClicked();
      }
    });
  }

  private String getParamTitle() {
    return IntentExtras.getStringExtra(this, PARAM_TITLE);
  }

  private String getParamDescription() {
    return IntentExtras.getStringExtra(this, PARAM_DESCRIPTION);
  }


}
