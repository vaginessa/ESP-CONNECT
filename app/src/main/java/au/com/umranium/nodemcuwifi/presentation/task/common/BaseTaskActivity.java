package au.com.umranium.nodemcuwifi.presentation.task.common;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.widget.TextView;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;

/**
 * A generic activity that performs a long running task.
 */
abstract public class BaseTaskActivity extends BaseActivity {

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

  // TODO: Eventually remove this
  abstract protected Intent createIntentForNextTask();

  // TODO: Eventually remove this
  public void proceedToNextTask() {
    startNextActivity(createIntentForNextTask());
  }

}
