package au.com.umranium.espconnect.presentation.display.aplist;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.AccessPointArrayRecyclerAdapter;
import au.com.umranium.espconnect.presentation.common.BaseActivity;
import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.presentation.tasks.connecting.ConnectingActivity;
import au.com.umranium.espconnect.presentation.utils.IntentExtras;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity that displays a list of ESP8266 access points for the user to pick one.
 */
public class AccessPointListActivity extends BaseActivity<AccessPointListController> implements AccessPointListController.Surface {

  private static final String PARAM_ACCESS_POINTS = "access_points";
  private RecyclerView list;
  private AccessPointArrayRecyclerAdapter adapter;

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull List<ScannedAccessPoint> accessPoints) {
    Intent intent = new Intent(context, AccessPointListActivity.class);
    ScannedAccessPoint[] accessPointsArr = accessPoints.toArray(new ScannedAccessPoint[accessPoints.size()]);
    intent.putExtra(PARAM_ACCESS_POINTS, accessPointsArr);
    return intent;
  }

  private List<ScannedAccessPoint> getAccessPointsFromIntent() {
    Parcelable[] accessPointArr = IntentExtras.getParcelableArrayExtra(this, PARAM_ACCESS_POINTS);
    ArrayList<ScannedAccessPoint> accessPoints = new ArrayList<>(accessPointArr.length);
    for (Parcelable parcelable : accessPointArr) {
      accessPoints.add((ScannedAccessPoint) parcelable);
    }
    return accessPoints;
  }

  @Override
  protected void doInjection() {
    DaggerAccessPointListComponent
        .builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .accessPointListModule(new AccessPointListModule(this, getAccessPointsFromIntent()))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_access_point_list);
    list = (RecyclerView) findViewById(R.id.ap_list);
    assert list != null;
    list.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  public void initListAdapter(Observer<ScannedAccessPoint> accessPointClickObserver) {
    adapter = new AccessPointArrayRecyclerAdapter(this, accessPointClickObserver, ScannedAccessPoint.SORT_BY_SIG_STRENGTH);
    list.setAdapter(adapter);
  }

  @Override
  public void showAccessPoints(List<ScannedAccessPoint> accessPoints) {
    adapter.populate(accessPoints);
  }

  @Override
  public void proceedToNextTask(ScannedAccessPoint accessPoint) {
    startNextActivity(ConnectingActivity.createIntent(this, accessPoint));
  }
}
