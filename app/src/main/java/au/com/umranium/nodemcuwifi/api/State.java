package au.com.umranium.nodemcuwifi.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author umran
 */
public class State implements Parcelable {

  @SerializedName("Soft_AP_IP")
  public String mSoftApIp;

  @SerializedName("Soft_AP_MAC")
  public String mSoftApMac;

  @SerializedName("Station_IP")
  public String mStationIp;

  @SerializedName("Station_MAC")
  public String mStationMac;

  @SerializedName("Password")
  public boolean mPassword;

  @SerializedName("SSID")
  public String mSsid;

  public boolean isStationIpBlank() {
    return mStationIp == null || mStationIp.isEmpty() || "0.0.0.0".equals(mStationIp);
  }

  @Override
  public String toString() {
    return "State{" +
        "mSoftApIp='" + mSoftApIp + '\'' +
        ", mSoftApMac='" + mSoftApMac + '\'' +
        ", mStationIp='" + mStationIp + '\'' +
        ", mStationMac='" + mStationMac + '\'' +
        ", mPassword=" + mPassword +
        ", mSsid='" + mSsid + '\'' +
        '}';
  }

  public State(String mSoftApIp, String mSoftApMac, String mStationIp, String mStationMac, boolean mPassword, String mSsid) {
    this.mSoftApIp = mSoftApIp;
    this.mSoftApMac = mSoftApMac;
    this.mStationIp = mStationIp;
    this.mStationMac = mStationMac;
    this.mPassword = mPassword;
    this.mSsid = mSsid;
  }

  protected State(Parcel in) {
    this.mSoftApIp = in.readString();
    this.mSoftApMac = in.readString();
    this.mStationIp = in.readString();
    this.mStationMac = in.readString();
    this.mPassword = in.readByte() != 0;
    this.mSsid = in.readString();
  }

  public State() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mSoftApIp);
    dest.writeString(this.mSoftApMac);
    dest.writeString(this.mStationIp);
    dest.writeString(this.mStationMac);
    dest.writeByte(this.mPassword ? (byte) 1 : (byte) 0);
    dest.writeString(this.mSsid);
  }

  public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
    @Override
    public State createFromParcel(Parcel source) {
      return new State(source);
    }

    @Override
    public State[] newArray(int size) {
      return new State[size];
    }
  };
}
