package au.com.umranium.espconnect.app.common;

import android.content.Context;
import android.support.annotation.StringRes;

import javax.inject.Inject;

import au.com.umranium.espconnect.di.qualifiers.AppInstance;

public class StringProvider {

  private final Context context;

  @Inject
  public StringProvider(@AppInstance Context context) {
    this.context = context;
  }

  public String getString(@StringRes int msg) {
    return context.getString(msg);
  }

  public String getString(@StringRes int msg, Object... args) {
    return context.getString(msg, args);
  }
}
