package au.com.umranium.nodemcuwifi.presentation.task.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import au.com.umranium.nodemcuwifi.R;

/**
 * A generic activity that performs a long running task.
 */
abstract public class BaseTaskActivity extends AppCompatActivity {

  private final BaseTaskController controller = createController();

  private TextView txtTitle;
  private TextView txtDescription;

  @NonNull
  protected abstract BaseTaskController createController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base_task);
    txtTitle = (TextView) findViewById(R.id.txt_title);
    assert txtTitle != null;
    txtDescription = (TextView) findViewById(R.id.txt_description);
    assert txtDescription != null;
    controller.onCreate();
  }

  @Override
  protected void onStart() {
    super.onStart();
    controller.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    controller.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    controller.onDestroy();
  }

  public void setTitle(@StringRes int title) {
    txtTitle.setText(title);
  }

  public void setMessage(@StringRes int message) {
    txtDescription.setText(message);
  }

  public void finishActivity() {
    finish();
  }

}
