package au.com.umranium.espconnect.common;

import android.os.Parcel;
import android.os.Parcelable;

public class ConfigDetails implements Parcelable {

  private final String ssid;
  private final String password;

  public ConfigDetails(String ssid, String password) {
    this.ssid = ssid;
    this.password = password;
  }

  protected ConfigDetails(Parcel in) {
    this.ssid = in.readString();
    this.password = in.readString();
  }

  public String getSsid() {
    return ssid;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.ssid);
    dest.writeString(this.password);
  }

  public static final Parcelable.Creator<ConfigDetails> CREATOR = new Parcelable.Creator<ConfigDetails>() {
    @Override
    public ConfigDetails createFromParcel(Parcel source) {
      return new ConfigDetails(source);
    }

    @Override
    public ConfigDetails[] newArray(int size) {
      return new ConfigDetails[size];
    }
  };
}
