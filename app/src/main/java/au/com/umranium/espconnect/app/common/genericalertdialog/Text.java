package au.com.umranium.espconnect.app.common.genericalertdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public class Text implements Parcelable {
  @Nullable
  @StringRes
  public Integer res;

  @Nullable
  public String value;

  public Text(@StringRes int res) {
    this.res = res;
    this.value = null;
  }

  public Text(@NonNull String value) {
    this.res = null;
    this.value = value;
  }

  public String getText(Context context) {
    if (res == null && value == null) {
      throw new IllegalStateException("Neither resource nor text is set!");
    }
    if (res != null) {
      return context.getString(res);
    } else {
      return value;
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.res);
    dest.writeString(this.value);
  }

  protected Text() {
  }

  protected Text(Parcel in) {
    this.res = (Integer) in.readValue(Integer.class.getClassLoader());
    this.value = in.readString();
  }

  public static final Creator<Text> CREATOR = new Creator<Text>() {
    @Override
    public Text createFromParcel(Parcel source) {
      return new Text(source);
    }

    @Override
    public Text[] newArray(int size) {
      return new Text[size];
    }
  };
}
