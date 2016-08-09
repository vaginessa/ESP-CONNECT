package au.com.umranium.espconnect.analytics;

import android.content.Context;
import android.support.annotation.StringRes;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.scope.AppScope;

/**
 * Provides usage tracking.
 */
@AppScope
public class Analytics {

  private final Context context;
  private final Tracker tracker;

  @Inject
  public Analytics(@Named("app") Context context) {
    this.context = context;
    GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
    analytics.enableAdvertisingIdCollection(true);
    this.tracker = analytics.newTracker(R.xml.app_tracker);
  }

  public void enterScreen(@StringRes int screenName) {
    tracker.setScreenName(context.getString(screenName));
    tracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  public void enterScreen(@StringRes int screenName, String additionalParams) {
    tracker.setScreenName(context.getString(screenName) + ":" + additionalParams);
    tracker.send(new HitBuilders.ScreenViewBuilder()
        .build());
  }

  public void leaveScreen() {
    tracker.setScreenName(null);
  }

  public void trackAction(@StringRes int category, @StringRes int actionIdentifier) {
    tracker.send(new HitBuilders.EventBuilder()
        .setCategory(context.getString(category))
        .setAction(context.getString(actionIdentifier))
        .build());
  }

  public void trackException(Throwable e) {
    tracker.send(new HitBuilders.ExceptionBuilder()
        .setFatal(false)
        .setDescription(new StandardExceptionParser(context, null)
            .getDescription(Thread.currentThread().getName(), e))
        .build());
  }

}
