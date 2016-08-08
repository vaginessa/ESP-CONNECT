package au.com.umranium.espconnect.presentation.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.umranium.espconnect.R;
import rx.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Recycler view list adapter for access point views.
 */
public class AccessPointArrayAdapter extends RecyclerView.Adapter<AccessPointViewHolder> {

  public static Comparator<ScannedAccessPoint> SORT_BY_SSID =
      new Comparator<ScannedAccessPoint>() {
        @Override
        public int compare(ScannedAccessPoint lhs, ScannedAccessPoint rhs) {
          // by case-insensitive alphabetic order
          return lhs.getSsid().toLowerCase().compareTo(rhs.getSsid().toLowerCase());
        }
      };
  public static Comparator<ScannedAccessPoint> SORT_BY_SIG_STRENGTH =
      new Comparator<ScannedAccessPoint>() {
        @Override
        public int compare(ScannedAccessPoint lhs, ScannedAccessPoint rhs) {
          return rhs.getSignalStrength() - lhs.getSignalStrength();
        }
      };


  private final Context mContext;
  private final Observer<ScannedAccessPoint> mClickedEvents;
  private final Comparator<ScannedAccessPoint> mAccessPointComparator;
  private final List<ScannedAccessPoint> mAccessPoints = new ArrayList<>();

  public AccessPointArrayAdapter(Context context, Observer<ScannedAccessPoint> clickedEvents, Comparator<ScannedAccessPoint> accessPointComparator) {
    this.mContext = context;
    this.mClickedEvents = clickedEvents;
    this.mAccessPointComparator = accessPointComparator;
    this.setHasStableIds(true);
  }

  @Override
  public AccessPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater
        .from(mContext)
        .inflate(R.layout.layout_scanned_access_point, parent, false);
    return new AccessPointViewHolder(mClickedEvents, itemView);
  }

  @Override
  public int getItemCount() {
    return mAccessPoints.size();
  }

  @Override
  public long getItemId(int position) {
    return mAccessPoints.get(position).getId();
  }

  @Override
  public void onBindViewHolder(AccessPointViewHolder holder, int position) {
    holder.bindTo(mAccessPoints.get(position));
  }

  public void populate(List<ScannedAccessPoint> accessPoints) {
    mAccessPoints.clear();
    mAccessPoints.addAll(accessPoints);
    Collections.sort(mAccessPoints, mAccessPointComparator);
    notifyDataSetChanged();
  }
}
