package au.com.umranium.nodemcuwifi.old.scanner;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.old.configurer.ConfigurerActivity;
import au.com.umranium.nodemcuwifi.utils.BootTimeUtils;
import au.com.umranium.nodemcuwifi.wifievents.*;
import permissions.dispatcher.*;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Fragment that shows a list of NodeMCU access points.
 */
@RuntimePermissions
@SuppressLint("CustomError")
public class ScanFragment extends Fragment {

  private static final String TAG = ScanFragment.class.getSimpleName();

  private static final String KEY_LAST_SCAN_REQUEST = "last_scan_request";
  private static final String KEY_LAST_SCAN_RESULTS = "last_scan_results";

  private static final String NODE_MCU_AP_FMT = "ESP.*";

  private static final String DIALOG_TAG = "dialog";

  private final PublishSubject<ScannedAccessPoint> mAccessPointClickEvents = PublishSubject.create();

  private AccessPointArrayAdapter mApAdapter;
  private WifiManager mWifiManager;
  private CompositeSubscription mSubscriptions;
  private Subscription mScanSubscription;
  private ViewGroup mScanningLayout;
  private ViewGroup mNoResultsLayout;
  private ViewGroup mResultsListLayout;

  private long mLastScanRequestTimestamp = -1;
  private long mLastScanResultsTimestamp = -1;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_scan, container, false);

    mScanningLayout = (ViewGroup) view.findViewById(R.id.layout_scanning);
    mNoResultsLayout = (ViewGroup) view.findViewById(R.id.layout_no_results);
    mResultsListLayout = (ViewGroup) view.findViewById(R.id.layout_results_list);
    mApAdapter = new AccessPointArrayAdapter(getContext(), mAccessPointClickEvents);

    RecyclerView list = (RecyclerView) view.findViewById(R.id.ap_list);
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    list.setAdapter(mApAdapter);

    mAccessPointClickEvents.subscribe(new Action1<ScannedAccessPoint>() {
      @Override
      public void call(ScannedAccessPoint scannedAccessPoint) {
        onAccessPointClicked(scannedAccessPoint);
      }
    });

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
    if (savedInstanceState != null) {
      mLastScanRequestTimestamp = savedInstanceState.getLong(KEY_LAST_SCAN_REQUEST, -1);
      mLastScanResultsTimestamp = savedInstanceState.getLong(KEY_LAST_SCAN_RESULTS, -1);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    mSubscriptions = new CompositeSubscription();

    // TODO: Remove dependency to the permission dispatcher library
    ScanFragmentPermissionsDispatcher.initialiseWifiScansWithCheck(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    ScanFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
        grantResults);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putLong(KEY_LAST_SCAN_REQUEST, mLastScanRequestTimestamp);
    outState.putLong(KEY_LAST_SCAN_RESULTS, mLastScanResultsTimestamp);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mScanSubscription != null) {
      mScanSubscription.unsubscribe();
      mScanSubscription = null;
    }
    mSubscriptions.unsubscribe();
  }

  /**
   * Starts a scan the first time and whenever a previous scan completes.
   */
  private Subscription startWifiScans() {
    return WifiEvents
        .getInstance()
        .getEvents()
        .ofType(WifiScanComplete.class)
        .startWith(WifiScanComplete.getInstance())// emit the first item
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<WifiScanComplete>() {
          @Override
          public void call(WifiScanComplete wifiScanComplete) {
            startScan();
            //Log.d(TAG, "wifi scan started");
          }
        });
  }

  private void startScan() {
    if (mLastScanRequestTimestamp < 0) {
      showScanningLayout();
    }
    mWifiManager.startScan();
    mLastScanRequestTimestamp = System.currentTimeMillis();
    mLastScanResultsTimestamp = -1;
  }

  void scanComplete() {
    mLastScanResultsTimestamp = System.currentTimeMillis();

    long scanRequestBootTimeMicros = 0;
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
      scanRequestBootTimeMicros = TimeUnit.MILLISECONDS.toMicros(
          BootTimeUtils.utcToTimeSinceBoot(mLastScanRequestTimestamp,
              BootTimeUtils.getDiffBootFromUtc()));
    }

    List<ScannedAccessPoint> accessPoints = new ArrayList<>();

    for (ScanResult scanResult : mWifiManager.getScanResults()) {
//            Log.d(TAG, "  scan-result: " + scanResult.SSID);

      if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
        if (scanResult.timestamp >= scanRequestBootTimeMicros) {
          continue;
        }
      }

      String name = scanResult.SSID;
      if (!name.matches(NODE_MCU_AP_FMT)) {
        continue;
      }

      int level = WifiManager.calculateSignalLevel(scanResult.level, 100);

      accessPoints.add(new ScannedAccessPoint(scanResult.BSSID, name, level));
    }

    if (!accessPoints.isEmpty()) {
      showResultsListLayout();
      mApAdapter.populate(accessPoints);
    } else {
      showNoResultsLayout();
    }
  }

  @NeedsPermission(permission.ACCESS_COARSE_LOCATION)
  void initialiseWifiScans() {
    // listen for wifi scan complete events
    Subscription scanCompleteSubscription = WifiEvents
        .getInstance()
        .getEvents()
        .ofType(WifiScanComplete.class)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<WifiScanComplete>() {
          @Override
          public void call(WifiScanComplete wifiScanComplete) {
            //Log.d(TAG, "wifi scan complete");
            scanComplete();
          }
        });
    mSubscriptions.add(scanCompleteSubscription);

    // if we can scan the wifi at any time...
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2 &&
        mWifiManager.isScanAlwaysAvailable()) {
      mSubscriptions.add(startWifiScans());
    } else {
      Subscription startScanWhenEnabled = WifiEvents
          .getInstance()
          .getStateEvents()
          .ofType(WifiStateEvent.class)
          .startWith(mWifiManager.isWifiEnabled() ? WifiEnabled.getInstance() :
              WifiDisabled.getInstance()) // emit the first state
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Action1<WifiStateEvent>() {
            @Override
            public void call(WifiStateEvent wifiState) {
              if (wifiState instanceof WifiEnabled) {
                // if enabled, initiate scan
                if (mScanSubscription == null) {
                  mScanSubscription = startWifiScans();
                }
              } else {
                // stop any future scans
                if (mScanSubscription != null) {
                  mScanSubscription.unsubscribe();
                  mScanSubscription = null;
                }
                // turn wifi on
                mWifiManager.setWifiEnabled(true);
              }
            }
          });
      mSubscriptions.add(startScanWhenEnabled);
    }
  }

  @OnShowRationale(permission.ACCESS_COARSE_LOCATION)
  void showRationaleForCourseLocation(final PermissionRequest request) {
    CourseLocationRationaleDialogFragment fragment =
        new CourseLocationRationaleDialogFragment();
    fragment.setRequest(request);
    fragment.show(getChildFragmentManager(), DIALOG_TAG);
  }

  @OnPermissionDenied(permission.ACCESS_COARSE_LOCATION)
  void showDeniedForCourseLocation() {
    handleLackOfCourseLocationPermission();
  }

  @OnNeverAskAgain(permission.ACCESS_COARSE_LOCATION)
  void showNeverAskForCourseLocation() {
    handleLackOfCourseLocationPermission();
  }

  private void handleLackOfCourseLocationPermission() {
    Toast.makeText(getContext(), R.string.message_no_course_location_permission,
        Toast.LENGTH_LONG)
        .show();
    getActivity().finish();
  }

  private void showScanningLayout() {
    mScanningLayout.setVisibility(View.VISIBLE);
    mNoResultsLayout.setVisibility(View.GONE);
    mResultsListLayout.setVisibility(View.GONE);
  }

  private void showNoResultsLayout() {
    mScanningLayout.setVisibility(View.GONE);
    mNoResultsLayout.setVisibility(View.VISIBLE);
    mResultsListLayout.setVisibility(View.GONE);
  }

  private void showResultsListLayout() {
    mScanningLayout.setVisibility(View.GONE);
    mNoResultsLayout.setVisibility(View.GONE);
    mResultsListLayout.setVisibility(View.VISIBLE);
  }

  private void onAccessPointClicked(ScannedAccessPoint accessPoint) {
    Intent intent = ConfigurerActivity.newIntent(getContext(), accessPoint.getSsid());
    startActivity(intent);
  }

}
