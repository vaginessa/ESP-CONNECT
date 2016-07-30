package au.com.umranium.nodemcuwifi.api.calls;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.State;
import rx.Observable;

public class StateCall extends AbstractCall<State> {

  @Inject
  public StateCall(Provider<NodeMcuService> serviceProvider) {
    super(serviceProvider);
  }

  @Override
  public Observable<State> call() {
    return serviceProvider
        .get()
        .getState();
  }

}
