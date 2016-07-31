package au.com.umranium.espconnect.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author umran
 */
public class ReceivedAccessPoints {

  @SerializedName("Access_Points")
  public List<ReceivedAccessPoint> mAccessPoints;

  public ReceivedAccessPoints() {
  }

  public ReceivedAccessPoints(List<ReceivedAccessPoint> mAccessPoints) {
    this.mAccessPoints = mAccessPoints;
  }

  @Override
  public String toString() {
    return "ReceivedAccessPoints{" +
        "mAccessPoints=" + mAccessPoints +
        '}';
  }

}
