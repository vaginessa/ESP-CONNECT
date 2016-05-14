package au.com.umranium.nodemcuwifi.presentation.aplist;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.task.connecting.ConnectingActivity;
import au.com.umranium.nodemcuwifi.presentation.utils.IntentExtras;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity that displays a list of ESP8266 access points for the user to pick one.
 */
public class AccessPointListActivity extends BaseActivity implements AccessPointListController.Surface {

  private static final String PARAM_ACCESS_POINTS = "access_points";
  private RecyclerView list;
  private AccessPointArrayAdapter adapter;

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

  @NonNull
  @Override
  protected BaseController createController() {
    return new AccessPointListController(this, getAccessPointsFromIntent());
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_access_point_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    list = (RecyclerView) findViewById(R.id.ap_list);
    assert list != null;
    list.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  public void initListAdapter(Observer<ScannedAccessPoint> accessPointClickObserver) {
    adapter = new AccessPointArrayAdapter(this, accessPointClickObserver);
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
