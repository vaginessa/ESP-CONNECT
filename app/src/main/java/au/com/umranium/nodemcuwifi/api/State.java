package au.com.umranium.nodemcuwifi.api;

import android.support.annotation.NonNull;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author umran
 */
public class State {

  @NonNull
  public final String mSoftApIp;

  @NonNull
  public final String mSoftApMac;

  @NonNull
  public final String mStationIp;

  @NonNull
  public final String mStationMac;

  @NonNull
  public final boolean mPassword;

  @NonNull
  public final String mSsid;

  public State(
      @NonNull String softApIp,
      @NonNull String softApMac,
      @NonNull String stationIp,
      @NonNull String stationMac,
      @NonNull boolean password,
      @NonNull String ssid) {
    mSoftApIp = softApIp;
    mSoftApMac = softApMac;
    mStationIp = stationIp;
    mStationMac = stationMac;
    mPassword = password;
    mSsid = ssid;
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

  public static class StateAdapter extends TypeAdapter<State> {

    private static final String MESSAGE_NAME = "State";

    @Override
    public void write(JsonWriter out, State value) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public State read(JsonReader in) throws IOException {
      in.beginObject();

      String softApIP = null;
      String softApMac = null;
      String stationIp = null;
      String stationMac = null;
      Boolean password = null;
      String ssid = null;

      while (in.peek() != JsonToken.END_OBJECT) {
        switch (in.nextName().toLowerCase()) {
          case "soft_ap_ip":
            softApIP = in.nextString();
            break;
          case "soft_ap_mac":
            softApMac = in.nextString();
            break;
          case "station_ip":
            stationIp = in.nextString();
            break;
          case "station_mac":
            stationMac = in.nextString();
            break;
          case "password":
            password = in.nextBoolean();
            break;
          case "ssid":
            ssid = in.nextString();
            break;
        }
      }

      if (softApIP == null) {
        throw new RequiredFieldNotProvided("Soft AP IP", MESSAGE_NAME);
      }
      if (softApMac == null) {
        throw new RequiredFieldNotProvided("Soft AP MAC", MESSAGE_NAME);
      }
      if (stationIp == null) {
        throw new RequiredFieldNotProvided("Station IP", MESSAGE_NAME);
      }
      if (stationMac == null) {
        throw new RequiredFieldNotProvided("Station MAC", MESSAGE_NAME);
      }
      if (password == null) {
        throw new RequiredFieldNotProvided("Password", MESSAGE_NAME);
      }
      if (ssid == null) {
        throw new RequiredFieldNotProvided("SSID", MESSAGE_NAME);
      }

      in.endObject();

      return new State(softApIP, softApMac, stationIp, stationMac, password, ssid);
    }
  }
}
