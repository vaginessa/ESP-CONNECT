package au.com.umranium.espconnect.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

/**
 * Throws a time-out exception if the first value is not emitted before a given amount of time.
 *
 * @param <T>
 */
public class TimeOut<T> implements Observable.Transformer<T, T> {

  public static class TimeOutException extends RuntimeException {
    public TimeOutException() {
    }
  }

  private static class Wrapper<T> {
    private final T value;

    public Wrapper(T value) {
      this.value = value;
    }
  }

  private final long delay;
  private final TimeUnit timeUnit;
  private final Scheduler scheduler;

  public TimeOut(long delay, TimeUnit timeUnit, Scheduler scheduler) {
    this.delay = delay;
    this.timeUnit = timeUnit;
    this.scheduler = scheduler;
  }

  @Override
  public Observable<T> call(Observable<T> in) {
    final Wrapper<T> timeOutObject = new Wrapper<>(null);
    final TimeOutException exception = new TimeOutException();
    final Observable<Wrapper<T>> timeOutObservable = Observable
        .timer(delay, timeUnit, scheduler)
        .map(ToInstance.instance(timeOutObject));

    return in
        .map(new Wrap<T>())
        .mergeWith(timeOutObservable)
        .flatMap(new WrapperObservableFunc1<>(timeOutObject, exception));
  }

  private static class Wrap<T> implements Func1<T, Wrapper<T>> {
    @Override
    public Wrapper<T> call(T t) {
      return new Wrapper<>(t);
    }
  }

  private static class WrapperObservableFunc1<T> implements Func1<Wrapper<T>, Observable<T>> {
    private final Wrapper<T> timeOutObject;
    private final TimeOutException exception;
    private volatile boolean hasReceivedValue;

    public WrapperObservableFunc1(Wrapper<T> timeOutObject, TimeOutException exception) {
      this.timeOutObject = timeOutObject;
      this.exception = exception;
      hasReceivedValue = false;
    }

    @Override
    synchronized public Observable<T> call(Wrapper<T> wrapper) {
      if (wrapper == timeOutObject) {
        if (!hasReceivedValue) {
          return Observable.error(exception);
        } else {
          return Observable.empty();
        }
      } else {
        hasReceivedValue = true;
        return Observable.just(wrapper.value);
      }
    }
  }
}
