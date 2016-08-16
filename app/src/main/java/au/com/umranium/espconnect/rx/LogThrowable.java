package au.com.umranium.espconnect.rx;

import android.util.Log;

import rx.functions.Action1;

public class LogThrowable implements Action1<Throwable> {

  private final String tag;
  private final String message;

  public LogThrowable(String tag, String message) {
    this.tag = tag;
    this.message = message;
  }

  @Override
  public void call(Throwable throwable) {
    Log.e(tag, message, throwable);
  }

}
