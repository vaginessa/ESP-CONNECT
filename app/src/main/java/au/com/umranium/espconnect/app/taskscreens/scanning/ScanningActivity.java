package au.com.umranium.espconnect.app.taskscreens.scanning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.app.displayscreens.aplist.AccessPointListActivity;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskActivity;
import au.com.umranium.espconnect.app.taskscreens.connecting.ConnectingActivity;
import au.com.umranium.espconnect.app.taskscreens.scanning.loc.CourseLocationRationaleDialogFragment;
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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ScanningActivityPermissionsDispatcher.ensureHasLocationPermissionWithCheck(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    ScanningActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }

  @Override
  public void proceedWithNoAccessPoints() {
    showErrorScreen(R.string.scanning_error_noap_title,
        R.string.scanning_error_noap_description);
  }

  @Override
  public void proceedWithSingleAccessPoint(ScannedAccessPoint accessPoint) {
    startNextActivity(ConnectingActivity.createIntent(this, accessPoint));
  }

  @Override
  public void proceedWithAccessPoints(List<ScannedAccessPoint> accessPoints) {
    Intent intent = AccessPointListActivity.createIntent(this, accessPoints);
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
