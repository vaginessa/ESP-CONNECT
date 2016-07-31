package au.com.umranium.espconnect.presentation.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A record of a detected access point.
 */
public class ScannedAccessPoint implements Parcelable {

  private final long id;
  private final String ssid;
  private final int signalStrength;

  public ScannedAccessPoint(long id, String ssid, int signalStrength) {
    this.id = id;
    this.ssid = ssid;
    this.signalStrength = signalStrength;
  }

  public ScannedAccessPoint(String bssid, String ssid, int signalStrength) {
    this.id = bssidToLong(bssid.toLowerCase());
    this.ssid = ssid;
    this.signalStrength = signalStrength;
  }

  protected ScannedAccessPoint(Parcel in) {
    this.id = in.readLong();
    this.ssid = in.readString();
    this.signalStrength = in.readInt();
  }

  public long getId() {
    return id;
  }

  public String getSsid() {
    return ssid;
  }

  public String getQuotedSsid() {
    return "\"" + ssid + "\"";
  }

  public int getSignalStrength() {
    return signalStrength;
  }

  @SuppressWarnings("ForLoopReplaceableByForEach")
  private static long bssidToLong(String bssid) {
    // ensure that it matches XX:XX:XX:...:XX
    if (!bssid.matches("\\p{XDigit}{2}(?::\\p{XDigit}{2})*")) {
      return -1;
    }

    String[] sections = bssid.split(":");
    long value = 0;
    for (int i = 0; i < sections.length; i++) {
      int sectionValue = Integer.parseInt(sections[i], 16);

      value <<= 8; // move by 1 byte
      value |= sectionValue & 0xFF;
    }

    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ScannedAccessPoint that = (ScannedAccessPoint) o;

    if (id != that.id) return false;
    if (signalStrength != that.signalStrength) return false;
    return ssid.equals(that.ssid);

  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + ssid.hashCode();
    result = 31 * result + signalStrength;
    return result;
  }

  @Override
  public String toString() {
    return "ScannedAccessPoint{" +
        "id=" + id +
        ", ssid='" + ssid + '\'' +
        ", signalStrength=" + signalStrength +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeString(this.ssid);
    dest.writeInt(this.signalStrength);
  }

  public static final Parcelable.Creator<ScannedAccessPoint> CREATOR = new Parcelable.Creator<ScannedAccessPoint>() {
    @Override
    public ScannedAccessPoint createFromParcel(Parcel source) {
      return new ScannedAccessPoint(source);
    }

    @Override
    public ScannedAccessPoint[] newArray(int size) {
      return new ScannedAccessPoint[size];
    }
  };
}
