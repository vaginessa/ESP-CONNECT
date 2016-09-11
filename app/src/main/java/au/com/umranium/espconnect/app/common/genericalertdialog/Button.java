package au.com.umranium.espconnect.app.common.genericalertdialog;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import au.com.umranium.espconnect.app.common.SerializableAction;

public class Button implements Parcelable {
  @NonNull
  public Text text;
  @NonNull
  public SerializableAction<?> eventHandler;

  protected Button() {
  }

  public Button(@NonNull Text text, @NonNull SerializableAction<?> eventHandler) {
    this.text = text;
    this.eventHandler = eventHandler;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.text, flags);
    dest.writeSerializable(this.eventHandler);
  }

  protected Button(Parcel in) {
    this.text = in.readParcelable(Text.class.getClassLoader());
    this.eventHandler = (SerializableAction) in.readSerializable();
  }

  public static final Creator<Button> CREATOR = new Creator<Button>() {
    @Override
    public Button createFromParcel(Parcel source) {
      return new Button(source);
    }

    @Override
    public Button[] newArray(int size) {
      return new Button[size];
    }
  };
}
