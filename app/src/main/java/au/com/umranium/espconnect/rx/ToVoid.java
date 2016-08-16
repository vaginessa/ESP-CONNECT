package au.com.umranium.espconnect.rx;

import rx.functions.Func1;

/**
 * Maps any value given to a {@link Void} value (effectively throwing away the value given).
 *
 * @author umran
 */
public final class ToVoid<T> implements Func1<T, Void> {

  private static volatile ToVoid sInstance;

  public synchronized static <T> ToVoid<T> toVoid() {
    if (sInstance == null) {
      sInstance = new ToVoid();
    }
    //noinspection unchecked
    return sInstance;
  }

  private ToVoid() {
    // Do nothing
  }

  @Override
  public Void call(T t) {
    return null;
  }
}
