package au.com.umranium.espconnect.presentation.tasks.common;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.widget.TextView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.presentation.common.BaseActivity;
import au.com.umranium.espconnect.presentation.common.BaseController;
import au.com.umranium.espconnect.presentation.display.error.ErrorActivity;

/**
 * A generic activity that performs a long running task.
 */
abstract public class BaseTaskActivity<BaseControllerType extends BaseController> extends BaseActivity<BaseControllerType> {

  private TextView txtTitle;
  private TextView txtDescription;

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_base_task);
    txtTitle = (TextView) findViewById(R.id.txt_title);
    assert txtTitle != null;
    txtDescription = (TextView) findViewById(R.id.txt_description);
    assert txtDescription != null;
  }

  public void setTitle(@StringRes int title) {
    txtTitle.setText(title);
  }

  public void setTitle(String title) {
    txtTitle.setText(title);
  }

  public void setMessage(@StringRes int message) {
    txtDescription.setText(message);
  }

  public void setMessage(String message) {
    txtDescription.setText(message);
  }

  public void showErrorScreen(@StringRes int title, @StringRes int message) {
    Intent intent = ErrorActivity.createIntent(this, getString(title), getString(message));
    startNextActivity(intent);
  }

  public void showErrorScreen(String title, String message) {
    ErrorActivity.createIntent(this, title, message);
  }
}
