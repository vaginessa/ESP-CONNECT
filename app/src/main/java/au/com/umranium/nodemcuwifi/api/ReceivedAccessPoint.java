package au.com.umranium.nodemcuwifi.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author umran
 */
public class ReceivedAccessPoint implements Parcelable {

  @SerializedName("SSID")
  public String mSsid;

  @SerializedName("Encryption")
  public boolean mEncryption;

  @SerializedName("Quality")
  public String mQuality;

  @Override
  public String toString() {
    return "ReceivedAccessPoint{" +
        "mSsid='" + mSsid + '\'' +
        ", mEncryption='" + mEncryption + '\'' +
        ", mQuality='" + mQuality + '\'' +
        '}';
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mSsid);
    dest.writeByte(this.mEncryption ? (byte) 1 : (byte) 0);
    dest.writeString(this.mQuality);
  }

  public ReceivedAccessPoint() {
  }

  public ReceivedAccessPoint(String mSsid, boolean mEncryption, String mQuality) {
    this.mSsid = mSsid;
    this.mEncryption = mEncryption;
    this.mQuality = mQuality;
  }

  protected ReceivedAccessPoint(Parcel in) {
    this.mSsid = in.readString();
    this.mEncryption = in.readByte() != 0;
    this.mQuality = in.readString();
  }

  public static final Parcelable.Creator<ReceivedAccessPoint> CREATOR = new Parcelable.Creator<ReceivedAccessPoint>() {
    @Override
    public ReceivedAccessPoint createFromParcel(Parcel source) {
      return new ReceivedAccessPoint(source);
    }

    @Override
    public ReceivedAccessPoint[] newArray(int size) {
      return new ReceivedAccessPoint[size];
    }
  };
}
