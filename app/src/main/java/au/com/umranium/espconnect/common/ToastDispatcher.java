package au.com.umranium.espconnect.common;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import javax.inject.Inject;

public class ToastDispatcher {

  private final Context context;

  @Inject
  public ToastDispatcher(Context context) {
    this.context = context;
  }

  public void showLongToast(@StringRes int msg) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
  }
}
