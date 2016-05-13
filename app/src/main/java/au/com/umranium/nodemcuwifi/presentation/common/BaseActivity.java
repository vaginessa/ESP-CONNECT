package au.com.umranium.nodemcuwifi.presentation.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * A generic activity from which other activities inherit from.
 */
abstract public class BaseActivity extends AppCompatActivity {

  private static final int NEXT_TASK_REQUEST_CODE = 1;

  private BaseController controller;

  @NonNull
  protected abstract BaseController createController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    controller = createController();
    initUi();
    controller.onCreate();
  }

  protected void initUi() {

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

  protected void startNextActivity(Intent intent) {
    startActivityForResult(intent, NEXT_TASK_REQUEST_CODE);
  }

  @SuppressWarnings("unused")
  public void finishTaskSuccessfully() {
    setResult(RESULT_OK);
    finish();
  }

  @SuppressWarnings("unused")
  public void cancelTask() {
    setResult(RESULT_CANCELED);
    finish();
  }

}