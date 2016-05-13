package au.com.umranium.nodemcuwifi.presentation.aplist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.utils.IntentExtras;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity that displays a list of ESP8266 access points for the user to pick one.
 */
public class AccessPointListActivity extends AppCompatActivity {

  private static final String PARAM_ACCESS_POINTS = "access_points";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @NonNull List<ScannedAccessPoint> accessPoints) {
    Intent intent = new Intent(context, AccessPointListActivity.class);
    ScannedAccessPoint[] accessPointsArr = accessPoints.toArray(new ScannedAccessPoint[accessPoints.size()]);
    intent.putExtra(PARAM_ACCESS_POINTS, accessPointsArr);
    return intent;
  }

  private final PublishSubject<ScannedAccessPoint> mAccessPointClickEvents = PublishSubject.create();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_access_point_list);


    RecyclerView list = (RecyclerView) findViewById(R.id.ap_list);
    assert list != null;
    list.setLayoutManager(new LinearLayoutManager(this));

    AccessPointArrayAdapter adapter = new AccessPointArrayAdapter(this, mAccessPointClickEvents);
    list.setAdapter(adapter);

    adapter.populate(getAccessPointsFromIntent());

    mAccessPointClickEvents.subscribe(new Action1<ScannedAccessPoint>() {
      @Override
      public void call(@NonNull ScannedAccessPoint accessPoint) {
        onAccessPointClicked(accessPoint);
      }
    });
  }

  private List<ScannedAccessPoint> getAccessPointsFromIntent() {
    Parcelable[] accessPointArr = IntentExtras.getParcelableArrayExtra(this, PARAM_ACCESS_POINTS);
    ArrayList<ScannedAccessPoint> accessPoints = new ArrayList<>(accessPointArr.length);
    for (Parcelable parcelable : accessPointArr) {
      accessPoints.add((ScannedAccessPoint) parcelable);
    }
    return accessPoints;
  }

  private void onAccessPointClicked(@NonNull ScannedAccessPoint accessPoint) {

  }

}
