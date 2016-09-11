package au.com.umranium.espconnect.app.common.genericalertdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import au.com.umranium.espconnect.app.common.SerializableAction;

class Params implements Parcelable {

  @NonNull
  public Text title;
  @NonNull
  public Text message;
  @NonNull
  public boolean cancelable;
  @Nullable
  public Button positiveButton;
  @Nullable
  public Button negativeButton;
  @Nullable
  public Button neutralButton;
  @Nullable
  public SerializableAction dismissAction;
  @Nullable
  public SerializableAction cancelAction;

  protected Params() {
  }

  void apply(AlertDialog.Builder builder, GenericAlertDialogFragment fragment) {
    Context context = fragment.getContext();
    builder.setTitle(title.getText(context));
    builder.setMessage(message.getText(context));
    fragment.setCancelable(cancelable);

    if (positiveButton != null) {
      builder.setPositiveButton(positiveButton.text.getText(context), fragment);
    }
    if (negativeButton != null) {
      builder.setNegativeButton(negativeButton.text.getText(context), fragment);
    }
    if (neutralButton != null) {
      builder.setNeutralButton(neutralButton.text.getText(context), fragment);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.title, flags);
    dest.writeParcelable(this.message, flags);
    dest.writeParcelable(this.positiveButton, flags);
    dest.writeParcelable(this.negativeButton, flags);
    dest.writeParcelable(this.neutralButton, flags);
    dest.writeSerializable(this.dismissAction);
    dest.writeSerializable(this.cancelAction);
  }

  protected Params(Parcel in) {
    this.title = in.readParcelable(Text.class.getClassLoader());
    this.message = in.readParcelable(Text.class.getClassLoader());
    this.positiveButton = in.readParcelable(Button.class.getClassLoader());
    this.negativeButton = in.readParcelable(Button.class.getClassLoader());
    this.neutralButton = in.readParcelable(Button.class.getClassLoader());
    this.dismissAction = (SerializableAction) in.readSerializable();
    this.cancelAction = (SerializableAction) in.readSerializable();
  }

  public static final Creator<Params> CREATOR = new Creator<Params>() {
    @Override
    public Params createFromParcel(Parcel source) {
      return new Params(source);
    }

    @Override
    public Params[] newArray(int size) {
      return new Params[size];
    }
  };
}
