package au.com.umranium.espconnect.app.common.genericalertdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.app.common.SerializableAction;

// TODO: Add instrumentation tests
public class GenericAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

  private static final String PARAMS = "PARAMS";

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Params params = getParams();
    Context context = getContext();
    AlertDialog.Builder builder = new AlertDialog.Builder(context, getTheme());
    params.apply(builder, this);
    return builder.create();
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    final Params params = getParams();
    BaseActivity baseActivity = getBaseActivity();
    switch (which) {
      case DialogInterface.BUTTON_POSITIVE:
        if (params.positiveButton != null) {
          //noinspection unchecked
          params.positiveButton.eventHandler.invoke(baseActivity);
        }
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        if (params.negativeButton != null) {
          //noinspection unchecked
          params.negativeButton.eventHandler.invoke(baseActivity);
        }
        break;
      case DialogInterface.BUTTON_NEUTRAL:
        if (params.neutralButton != null) {
          //noinspection unchecked
          params.neutralButton.eventHandler.invoke(baseActivity);
        }
        break;
    }
  }

  private BaseActivity getBaseActivity() {
    Activity activity = getActivity();
    if (!(activity instanceof BaseActivity)) {
      throw new IllegalArgumentException("Unsupported activity type:" + activity.getClass().getCanonicalName());
    }
    return (BaseActivity) activity;
  }

  @NonNull
  private Params getParams() {
    final Params params = getArguments().getParcelable(PARAMS);
    if (params == null) {
      throw new IllegalArgumentException("Parameters not set!");
    }
    return params;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    final Params params = getParams();
    if (params.dismissAction != null) {
      BaseActivity baseActivity = getBaseActivity();
      //noinspection unchecked
      params.dismissAction.invoke(baseActivity);
    }
    super.onDismiss(dialog);
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    final Params params = getParams();
    if (params.cancelAction != null) {
      BaseActivity baseActivity = getBaseActivity();
      //noinspection unchecked
      params.cancelAction.invoke(baseActivity);
    }
    super.onCancel(dialog);
  }

  public static class Builder {
    private final Params params;

    public Builder(Text title, Text message) {
      params = new Params();
      params.title = title;
      params.message = message;
    }

    public Builder setPositiveButton(Button positiveButton) {
      params.positiveButton = positiveButton;
      return this;
    }

    public Builder setNegativeButton(Button negativeButton) {
      params.negativeButton = negativeButton;
      return this;
    }

    public Builder setNeutralButton(Button neutralButton) {
      params.neutralButton = neutralButton;
      return this;
    }

    /**
     * @see android.support.v7.app.AlertDialog#setCancelable(boolean)
     */
    public Builder setCancelable(boolean cancelable) {
      params.cancelable = cancelable;
      return this;
    }

    /**
     * @see android.support.v7.app.AlertDialog.Builder#setOnDismissListener(DialogInterface.OnDismissListener)
     */
    public Builder setDismissAction(SerializableAction action) {
      params.dismissAction = action;
      return this;
    }

    /**
     * @see android.support.v7.app.AlertDialog.Builder#setOnCancelListener(DialogInterface.OnCancelListener)
     */
    public Builder setCancelAction(SerializableAction action) {
      params.cancelAction = action;
      return this;
    }

    public GenericAlertDialogFragment create() {
      Bundle args = new Bundle();
      args.putParcelable(PARAMS, params);
      GenericAlertDialogFragment fragment = new GenericAlertDialogFragment();
      fragment.setArguments(args);
      return fragment;
    }
  }


}
