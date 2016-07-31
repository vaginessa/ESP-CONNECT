package au.com.umranium.espconnect.api.calls;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.espconnect.api.NodeMcuService;
import rx.Observable;

public class SaveCall extends AbstractCall<Void> {

  private final String ssid;
  private final String password;

  @Inject
  public SaveCall(Provider<NodeMcuService> serviceProvider, String ssid, String password) {
    super(serviceProvider);
    this.ssid = ssid;
    this.password = password;
  }

  @Override
  public Observable<Void> call() {
    return serviceProvider
        .get()
        .save(ssid, password);
  }

}
