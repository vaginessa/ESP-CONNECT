package au.com.umranium.espconnect.rx;

import au.com.umranium.espconnect.di.scope.AppScope;
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

  public rx.Scheduler computation() {
    return Schedulers.computation();
  }

}
