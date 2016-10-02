package au.com.umranium.espconnect.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.scope.ActivityScope;

import javax.inject.Inject;

/**
 * A generic activity from which other activities inherit from.
 */
abstract public class BaseActivity<BaseControllerType extends BaseController> extends AppCompatActivity {

  private static final int NEXT_TASK_REQUEST_CODE = 1;
  private static final int RESULT_EXIT_APP = 2;

  @Inject
  @ActivityScope
  protected BaseControllerType controller;

  private boolean exiting = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    doInjection();
    initUi();
    controller.onCreate();
  }

  protected App getApp() {
    return (App) getApplication();
  }

  protected void doInjection() {

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
        case RESULT_EXIT_APP:
          closeApp();
          break;
      }
      return;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  protected void startNextActivity(Intent intent) {
    startActivityForResult(intent, NEXT_TASK_REQUEST_CODE);
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
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

  @SuppressWarnings("unused")
  public void closeApp() {
    setResult(RESULT_EXIT_APP);
    exiting = true;
    finish();
  }

  public void openUriInBrowser(Uri uri) {
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  @Override
  public void finish() {
    super.finish();
    if (!exiting) {
      overrideFinishingTransition();
    }
  }

  protected void overrideFinishingTransition() {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
  }

  public BaseControllerType getController() {
    return controller;
  }
}
