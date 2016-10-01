package au.com.umranium.espconnect.api.calls;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.espconnect.api.NodeMcuService;
import au.com.umranium.espconnect.api.data.State;
import rx.Observable;

public class CloseCall extends AbstractCall<Void> {

  @Inject
  public CloseCall(Provider<NodeMcuService> serviceProvider) {
    super(serviceProvider);
  }

  @Override
  public Observable<Void> call() {
    return serviceProvider
        .get()
        .close();
  }

}
