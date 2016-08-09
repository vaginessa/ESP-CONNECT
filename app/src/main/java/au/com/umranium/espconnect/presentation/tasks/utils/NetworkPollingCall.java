package au.com.umranium.espconnect.presentation.tasks.utils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.umranium.espconnect.presentation.common.Scheduler;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Returns an observable that continuously makes an API call for a given amount of seconds at specified number of intervals,
 * and emits the result until when unsubscribed from, ignoring any errors that the error filter function returns true for.
 * <p/>
 * Throws an exception if the call is not successful within the specified number of intervals. Hence, if the interval is 1 second,
 * and number of periods is 10, an error will be thrown if not successful after 10 seconds.
 */
public class NetworkPollingCall<R> implements Func0<Observable<R>> {

  public static class MaxRetryException extends RuntimeException {
    public MaxRetryException() {
    }

    public MaxRetryException(Throwable throwable) {
      super(throwable);
    }
  }

  private final long intervalSeconds;
  private final int maxCallCount;
  private final WifiConnectionUtil wifiConnectionUtil;
  private final Scheduler scheduler;
  private final Func0<Observable<R>> callFunction;
  private final Func1<Throwable, Boolean> errorFilterFunction;

  @Inject
  public NetworkPollingCall(@Named("intervalSeconds") long intervalSeconds, @Named("maxCallCount") int maxCallCount, WifiConnectionUtil wifiConnectionUtil, Scheduler scheduler, Func0<Observable<R>> callFunction, Func1<Throwable, Boolean> errorFilterFunction) {
    this.intervalSeconds = intervalSeconds;
    this.maxCallCount = maxCallCount;
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.scheduler = scheduler;
    this.callFunction = callFunction;
    this.errorFilterFunction = errorFilterFunction;
  }

  @Override
  public Observable<R> call() {
    return makeCall(0);
  }

  public Observable<R> makeCall(final int prevCallCount) {
    return Observable.interval(intervalSeconds, TimeUnit.SECONDS, scheduler.computation())
        .map(new ToTimerEventCount())
        .startWith(1)
        .doOnNext(new CheckMaxCallCount(prevCallCount))
        .filter(wifiConnectionUtil.new IsConnectedToEsp<>())
        .filter(wifiConnectionUtil.new IsTrackingWifiNetwork<>())
        .take(1)
        .switchMap(new Func1<Integer, Observable<R>>() {
          @Override
          public Observable<R> call(final Integer tickCount) {
            return callFunction
                .call()
                .subscribeOn(scheduler.io())
                .onErrorResumeNext(new HandleErrors(tickCount + prevCallCount));
          }
        });
  }

  private static class ToTimerEventCount implements Func1<Long, Integer> {
    @Override
    public Integer call(Long tickIndex) {
      return (int) (tickIndex + 2);
    }
  }

  private class MapToNextCall implements Func1<Long, Observable<R>> {
    private final Integer callCount;

    public MapToNextCall(Integer callCount) {
      this.callCount = callCount;
    }

    @Override
    public Observable<R> call(Long ignore) {
      return makeCall(callCount);
    }
  }

  private class HandleErrors implements Func1<Throwable, Observable<? extends R>> {
    private final Integer callCount;

    public HandleErrors(Integer callCount) {
      this.callCount = callCount;
    }

    @Override
    public Observable<? extends R> call(Throwable error) {
      if (callCount >= maxCallCount) {
        return Observable.error(new MaxRetryException(error));
      } else {
        if (errorFilterFunction.call(error)) {
          // if ignorable error, make another call after the waiting interval
          return Observable.timer(intervalSeconds, TimeUnit.SECONDS, scheduler.computation())
              .switchMap(new MapToNextCall(callCount));
        } else {
          return Observable.error(error);
        }
      }
    }
  }

  private class CheckMaxCallCount implements Action1<Integer> {
    private final int prevCallCount;

    public CheckMaxCallCount(int prevCallCount) {
      this.prevCallCount = prevCallCount;
    }

    @Override
    public void call(Integer tickCount) {
      if (prevCallCount +tickCount>maxCallCount) {
        throw new MaxRetryException();
      }
    }
  }
}
