package au.com.umranium.espconnect.app.taskscreens.scanning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.util.List;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.app.common.SerializableAction;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.common.genericalertdialog.Button;
import au.com.umranium.espconnect.app.common.genericalertdialog.GenericAlertDialogFragment;
import au.com.umranium.espconnect.app.common.genericalertdialog.Text;
import au.com.umranium.espconnect.app.displayscreens.aplist.AccessPointListActivity;
import au.com.umranium.espconnect.app.taskscreens.BaseTaskActivity;
import au.com.umranium.espconnect.app.taskscreens.connecting.ConnectingActivity;
import au.com.umranium.espconnect.app.taskscreens.scanning.loc.CourseLocationRationaleDialogFragment;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * An activity that scans for ESP8266 nodes.
 */
@RuntimePermissions
@SuppressLint("CustomError")
public class ScanningActivity extends BaseTaskActivity<ScanningController> implements ScanningController.Surface {

  private static final String COURSE_LOC_RATIONALE_DIALOG = "COURSE_LOC_RATIONALE_DIALOG";
  private static final String TURN_WIFI_ON_REQUEST_DIALOG = "TURN_WIFI_ON_REQUEST_DIALOG";
  private static final String TURN_AP_OFF_REQUEST_DIALOG = "TURN_AP_OFF_REQUEST_DIALOG";

  private static final String EXTRA_LOCATION_ACCEPT_ACTION = "EXTRA_LOCATION_ACCEPT_ACTION";
  private static final String EXTRA_LOCATION_DENY_ACTION = "EXTRA_LOCATION_DENY_ACTION";
  private static final String EXTRA_LOCATION_PERM_DENY_ACTION = "EXTRA_LOCATION_PERM_DENY_ACTION";

  private SerializableAction<ScanningController> locOnAcceptAction;
  private SerializableAction<ScanningController> locOnDenyAction;
  private SerializableAction<ScanningController> locOnPermDenyAction;

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
    setAnimation(R.drawable.scanning_animation);
    setProgressCurrentStep(1);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    ScanningActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(EXTRA_LOCATION_ACCEPT_ACTION, locOnAcceptAction);
    outState.putSerializable(EXTRA_LOCATION_DENY_ACTION, locOnDenyAction);
    outState.putSerializable(EXTRA_LOCATION_PERM_DENY_ACTION, locOnPermDenyAction);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    //noinspection unchecked
    locOnAcceptAction = (SerializableAction<ScanningController>) savedInstanceState.getSerializable(EXTRA_LOCATION_ACCEPT_ACTION);
    //noinspection unchecked
    locOnDenyAction = (SerializableAction<ScanningController>) savedInstanceState.getSerializable(EXTRA_LOCATION_DENY_ACTION);
    //noinspection unchecked
    locOnPermDenyAction = (SerializableAction<ScanningController>) savedInstanceState.getSerializable(EXTRA_LOCATION_PERM_DENY_ACTION);
  }

  @Override
  public void ensureLocationPermissions(SerializableAction<ScanningController> onAcceptAction,
                                        SerializableAction<ScanningController> onDenyAction,
                                        SerializableAction<ScanningController> onPermDenyAction) {
    locOnAcceptAction = onAcceptAction;
    locOnDenyAction = onDenyAction;
    locOnPermDenyAction = onPermDenyAction;
    // TODO: Get rid of this library!
    ScanningActivityPermissionsDispatcher.ensureHasLocationPermissionWithCheck(this);
  }

  @Override
  public void requestUserToTurnWifiOn(SerializableAction<ScanningController> onAcceptAction,
                                      SerializableAction<ScanningController> onRejectAction) {
    GenericAlertDialogFragment fragment = (GenericAlertDialogFragment) getSupportFragmentManager().findFragmentByTag(TURN_WIFI_ON_REQUEST_DIALOG);
    if (fragment == null) {
      fragment =
          new GenericAlertDialogFragment.Builder(
              new Text(R.string.turn_wifi_on_request_dlg_title),
              new Text(R.string.turn_wifi_on_request_dlg_msg)
          )
              .setPositiveButton(new Button(
                  new Text(R.string.turn_wifi_on_request_dlg_pos_btn),
                  onAcceptAction
              ))
              .setNegativeButton(new Button(
                  new Text(R.string.turn_wifi_on_request_dlg_neg_btn),
                  onRejectAction
              ))
              .setCancelable(false)
              .create();
      fragment.show(getSupportFragmentManager(), TURN_WIFI_ON_REQUEST_DIALOG);
    }
  }

  @Override
  public void requestUserToTurnOffAccessPoint(SerializableAction<ScanningController> onAcceptAction,
                                              SerializableAction<ScanningController> onRejectAction) {
    GenericAlertDialogFragment fragment = (GenericAlertDialogFragment) getSupportFragmentManager().findFragmentByTag(TURN_AP_OFF_REQUEST_DIALOG);
    if (fragment == null) {
      fragment =
          new GenericAlertDialogFragment.Builder(
              new Text(R.string.turn_access_point_off_request_dlg_title),
              new Text(R.string.turn_access_point_off_request_dlg_msg)
          )
              .setPositiveButton(new Button(
                  new Text(R.string.turn_access_point_off_request_dlg_pos_btn),
                  onAcceptAction
              ))
              .setNegativeButton(new Button(
                  new Text(R.string.turn_access_point_off_request_dlg_neg_btn),
                  onRejectAction
              ))
              .setCancelable(false)
              .create();
      fragment.show(getSupportFragmentManager(), TURN_AP_OFF_REQUEST_DIALOG);
    }
  }

  @Override
  public void sendUserToWifiSettings() {
    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
    startActivity(intent);
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
    if (locOnAcceptAction == null) {
      throw new IllegalStateException("Location permission accepted but action not stored!");
    }
    locOnAcceptAction.invoke(this);
    clearLocPermissionActions();
  }

  @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void showCourseLocationRationale(PermissionRequest request) {
    CourseLocationRationaleDialogFragment fragment =
        new CourseLocationRationaleDialogFragment();
    fragment.setRequest(request);
    fragment.show(getSupportFragmentManager(), COURSE_LOC_RATIONALE_DIALOG);
  }

  @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void handleNoCourseLocationPermission() {
    if (locOnDenyAction == null) {
      throw new IllegalStateException("Location denied accepted but action not stored!");
    }
    locOnDenyAction.invoke(this);
    clearLocPermissionActions();
  }

  @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
  public void handlePermNoCourseLocationPermission() {
    if (locOnPermDenyAction == null) {
      throw new IllegalStateException("Location permanently denied accepted but action not stored!");
    }
    locOnPermDenyAction.invoke(this);
    clearLocPermissionActions();
  }

  private void clearLocPermissionActions() {
    locOnAcceptAction = null;
    locOnDenyAction = null;
    locOnPermDenyAction = null;
  }

}
