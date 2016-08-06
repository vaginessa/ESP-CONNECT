package au.com.umranium.espconnect.analytics;

import rx.functions.Action1;

/**
 * Logs a thrown exception to tracker.
 */
public class LoggingErrorAction implements Action1<Throwable> {

  private final ErrorTracker tracker;

  public LoggingErrorAction(ErrorTracker tracker) {
    this.tracker = tracker;
  }

  @Override
  public void call(Throwable e) {
    tracker.onException(e);
  }
}
