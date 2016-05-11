package au.com.umranium.nodemcuwifi.utils.rx;

import rx.functions.Func1;

/**
 * Maps any value given to a single value provided at initialisation.
 *
 * @author umran
 */
public final class ToInstance<T, R> implements Func1<T, R> {

  public static <T, R> ToInstance<T, R> getInstance(R returnValue) {
    return new ToInstance<>(returnValue);
  }

  private R returnValue;

  private ToInstance(R returnValue) {
    this.returnValue = returnValue;
  }

  @Override
  public R call(T t) {
    return returnValue;
  }
}
