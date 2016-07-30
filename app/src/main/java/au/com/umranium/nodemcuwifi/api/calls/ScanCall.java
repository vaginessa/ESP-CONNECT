package au.com.umranium.nodemcuwifi.api.calls;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoints;
import rx.Observable;
import rx.functions.Action1;

public class ScanCall extends AbstractCall<ReceivedAccessPoints> {

  @Inject
  public ScanCall(Provider<NodeMcuService> serviceProvider) {
    super(serviceProvider);
  }

  @Override
  public Observable<ReceivedAccessPoints> call() {
    Log.d("SCANCALL", "about to start scan");
    return serviceProvider
        .get()
        .scan()
        .doOnNext(new Action1<ReceivedAccessPoints>() {
          @Override
          public void call(ReceivedAccessPoints receivedAccessPoints) {
            Log.d("SCANCALL", "scan complete: "+receivedAccessPoints.mAccessPoints.size());
          }
        });
  }

}
