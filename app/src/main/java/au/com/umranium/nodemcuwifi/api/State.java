package au.com.umranium.nodemcuwifi.api;

import com.google.gson.annotations.SerializedName;

/**
 * @author umran
 */
public class State {

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

}
