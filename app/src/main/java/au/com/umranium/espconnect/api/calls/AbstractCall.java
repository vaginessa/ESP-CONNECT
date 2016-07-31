package au.com.umranium.espconnect.api.calls;

import javax.inject.Provider;

import au.com.umranium.espconnect.api.NodeMcuService;
import rx.Observable;
import rx.functions.Func0;

public abstract class AbstractCall<T> implements Func0<Observable<T>> {

  protected final Provider<NodeMcuService> serviceProvider;

  public AbstractCall(Provider<NodeMcuService> serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  @Override
  public abstract Observable<T> call();

}
