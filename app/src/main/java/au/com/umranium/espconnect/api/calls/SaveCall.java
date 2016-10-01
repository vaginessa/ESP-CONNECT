package au.com.umranium.espconnect.api.calls;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.espconnect.api.NodeMcuService;
import rx.Observable;

public class SaveCall extends AbstractCall<Void> {

  @NonNull
  private final String ssid;
  @Nullable
  private final String password;

  @Inject
  public SaveCall(Provider<NodeMcuService> serviceProvider, @NonNull String ssid, @Nullable String password) {
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
