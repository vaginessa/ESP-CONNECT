package au.com.umranium.espconnect.app.common;

import rx.Observable;
import rx.functions.Func1;

public class ThrowableToDisplayableError<T> implements Func1<Throwable, Observable<T>> {

  public static <T> ThrowableToDisplayableError<T> create(String message) {
    return new ThrowableToDisplayableError<>(message);
  }

  private final String message;

  public ThrowableToDisplayableError(String message) {
    this.message = message;
  }

  @Override
  public Observable<T> call(Throwable throwable) {
    return Observable.error(new DisplayableError(message, throwable));
  }

}
