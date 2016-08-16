package au.com.umranium.espconnect.app.common;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import javax.inject.Inject;

import au.com.umranium.espconnect.di.qualifiers.ActivityInstance;

public class ToastDispatcher {

  private final Context context;

  @Inject
  public ToastDispatcher(@ActivityInstance Context context) {
    this.context = context;
  }

  public void showLongToast(@StringRes int msg) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
  }
}
