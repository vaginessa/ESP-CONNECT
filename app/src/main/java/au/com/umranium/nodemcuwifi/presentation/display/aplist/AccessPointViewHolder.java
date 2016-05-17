package au.com.umranium.nodemcuwifi.presentation.display.aplist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import rx.Observer;

import java.util.Locale;

/**
 * Recycler view holder for access points.
 */
class AccessPointViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  private final Observer<ScannedAccessPoint> mClickedEvents;
  private final TextView mName;
  private final TextView mSigStrength;
  private ScannedAccessPoint mBoundAccessPoint;

  public AccessPointViewHolder(Observer<ScannedAccessPoint> clickedEvents, View itemView) {
    super(itemView);
    this.mClickedEvents = clickedEvents;

    mName = (TextView) itemView.findViewById(R.id.txt_name);
    mSigStrength = (TextView) itemView.findViewById(R.id.txt_sig_strength);

    itemView.setOnClickListener(this);
  }

  public void bindTo(ScannedAccessPoint accessPoint) {
    mName.setText(accessPoint.getSsid());
    mSigStrength.setText(String.format(Locale.US, "%d%%", accessPoint.getSignalStrength()));
    itemView.setSelected(true);
    this.mBoundAccessPoint = accessPoint;
  }

  @Override
  public void onClick(View v) {
    mClickedEvents.onNext(mBoundAccessPoint);
  }
}
