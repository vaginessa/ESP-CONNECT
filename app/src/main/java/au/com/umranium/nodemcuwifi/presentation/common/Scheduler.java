package au.com.umranium.nodemcuwifi.presentation.common;

import au.com.umranium.nodemcuwifi.di.scope.AppScope;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@AppScope
public class Scheduler {

  public rx.Scheduler mainThread() {
    return AndroidSchedulers.mainThread();
  }

  public rx.Scheduler io() {
    return Schedulers.io();
  }

}
