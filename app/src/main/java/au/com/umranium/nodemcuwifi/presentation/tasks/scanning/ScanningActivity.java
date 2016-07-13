package au.com.umranium.nodemcuwifi.presentation.tasks.scanning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.display.aplist.AccessPointListActivity;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.scanning.loc.CourseLocationRationaleDialogFragment;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import java.util.List;

/**
 * An activity that scans for ESP8266 nodes.
 */
@RuntimePermissions
@SuppressLint("CustomError")
public class ScanningActivity extends BaseTaskActivity<ScanningController> implements ScanningController.Surface {

  private static final String DIALOG_TAG = "dialog";

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ScanningActivity.class);
  }

  @Override
  protected void doInjection() {
    DaggerScanningComponent.builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .scanningModule(new ScanningModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected Intent createIntentForNextTask() {
    throw new RuntimeException();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ScanningActivityPermissionsDispatcher.ensureHasLocationPermissionWithCheck(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    ScanningActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }

  @Override
  public void proceedWithAccessPoints(List<ScannedAccessPoint> accessPoints) {
    Intent intent = AccessPointListActivity.createIntent(this, accessPoints);
    startNextActivity(intent);
  }

  @Override
  public void proceedWithNoAccessPoints() {
    Intent intent = ErrorActivity.createIntent(this,
        R.string.scanning_error_noap_title,
        R.string.scanning_error_noap_description);
    startNextActivity(intent);
  }

  @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void ensureHasLocationPermission() {
    controller.startScanning();
  }

  @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void showCourseLocationRationale(PermissionRequest request) {
    CourseLocationRationaleDialogFragment fragment =
        new CourseLocationRationaleDialogFragment();
    fragment.setRequest(request);
    fragment.show(getSupportFragmentManager(), DIALOG_TAG);
  }

  @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void handleNoCourseLocationPermission() {
    controller.handleDeniedLocationPermission();
  }

  @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void handlePermNoCourseLocationPermission() {
    controller.handlePermanentlyDeniedLocationPermission();
  }

}
