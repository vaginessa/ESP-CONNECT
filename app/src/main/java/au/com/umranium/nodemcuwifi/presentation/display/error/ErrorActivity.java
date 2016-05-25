package au.com.umranium.nodemcuwifi.presentation.display.error;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.utils.IntentExtras;

/**
 * An generic activity that displays errors.
 */
public class ErrorActivity extends BaseActivity<ErrorController> implements ErrorController.Surface {

  private static final String PARAM_TITLE = "title";
  private static final String PARAM_DESCRIPTION = "description";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @StringRes int title,
                                    @StringRes int description) {
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
        .errorModule(new ErrorModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_error);

    int title = IntentExtras.getIntExtra(this, PARAM_TITLE);
    int description = IntentExtras.getIntExtra(this, PARAM_DESCRIPTION);

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

  @Override
  public void proceedToPrevScreen() {
    ErrorActivity.this.finish();
  }
}
