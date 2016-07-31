package au.com.umranium.espconnect.api;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import rx.functions.Func1;

/**
 * A function that can be used to ignore socket exceptions and throw all other exceptions.
 */
public class IgnoreSocketErrorFunction implements Func1<Throwable, Boolean> {

  @Override
  public Boolean call(Throwable error) {
    if (error instanceof SocketException ||
        error instanceof SocketTimeoutException) {
      return true;
    } else {
      return false;
    }
  }
}
