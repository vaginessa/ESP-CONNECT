package au.com.umranium.nodemcuwifi.presentation.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import au.com.umranium.nodemcuwifi.R;
import rx.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Recycler view list adapter for access point views.
 */
public class AccessPointArrayAdapter extends RecyclerView.Adapter<AccessPointViewHolder> {

  private static Comparator<ScannedAccessPoint> ACCESS_POINT_SORT_ORDER =
      new Comparator<ScannedAccessPoint>() {
        @Override
        public int compare(ScannedAccessPoint lhs, ScannedAccessPoint rhs) {
          // by case-insensitive alphabetic order
          return lhs.getSsid().toLowerCase().compareTo(rhs.getSsid().toLowerCase());
        }
      };


  private final Context mContext;
  private final Observer<ScannedAccessPoint> mClickedEvents;
  private final List<ScannedAccessPoint> mAccessPoints = new ArrayList<>();

  public AccessPointArrayAdapter(Context context, Observer<ScannedAccessPoint> clickedEvents) {
    this.mContext = context;
    this.mClickedEvents = clickedEvents;
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
    Collections.sort(mAccessPoints, ACCESS_POINT_SORT_ORDER);
    notifyDataSetChanged();
  }
}
