package au.com.umranium.nodemcuwifi.presentation.task.common;

import android.app.Activity;
import android.content.Intent;
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

  private static final int NEXT_TASK_REQUEST_CODE = 1;

  private BaseTaskController controller;
  private TextView txtTitle;
  private TextView txtDescription;

  @NonNull
  protected abstract BaseTaskController createController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    controller = createController();
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

  @Override
  public void onBackPressed() {
    controller.backPressed();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == NEXT_TASK_REQUEST_CODE) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          controller.nextTaskCompleted();
          break;
        case Activity.RESULT_CANCELED:
          controller.nextTaskWasCancelled();
          break;
      }
      return;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void setTitle(@StringRes int title) {
    txtTitle.setText(title);
  }

  public void setMessage(@StringRes int message) {
    txtDescription.setText(message);
  }

  // TODO: Eventually remove this
  abstract protected Intent createIntentForNextTask();

  // TODO: Eventually remove this
  public void proceedToNextTask() {
    startNextActivity(createIntentForNextTask());
  }

  protected void startNextActivity(Intent intent) {
    startActivityForResult(intent, NEXT_TASK_REQUEST_CODE);
  }

  public void finishTaskSuccessfully() {
    setResult(RESULT_OK);
    finish();
  }

  public void cancelTask() {
    setResult(RESULT_CANCELED);
    finish();
  }

}
