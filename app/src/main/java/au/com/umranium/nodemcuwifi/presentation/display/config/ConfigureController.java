package au.com.umranium.nodemcuwifi.presentation.display.config;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.inject.Inject;
import java.util.List;

/**
 * Controller for {@link ConfigureActivity}
 */
public class ConfigureController extends BaseController<ConfigureController.Surface> {

  private final PublishSubject<ScannedAccessPoint> accessPointClickEvents = PublishSubject.create();

  private final ScannedAccessPoint accessPoint;
  private final List<ScannedAccessPoint> ssids;

  @Inject
  public ConfigureController(ConfigureController.Surface surface,
                             ScannedAccessPoint accessPoint,
                             List<ScannedAccessPoint> ssids) {
    super(surface);
    this.accessPoint = accessPoint;
    this.ssids = ssids;
  }

  @Override
  public void onCreate() {
    surface.initListAdapter(accessPointClickEvents);

    accessPointClickEvents.subscribe(new Action1<ScannedAccessPoint>() {
      @Override
      public void call(@NonNull ScannedAccessPoint accessPoint) {
        onAccessPointClicked(accessPoint);
      }
    });

    surface.showSsids(ssids);
  }

  public void onSubmit(String ssid, String password) {
    surface.clearErrors();
    if (ssid.isEmpty()) {
      surface.showSsidError(R.string.configure_error_blank_ssid);
      return;
    }
    if (password.isEmpty()) {
      surface.showPasswordError(R.string.configure_error_blank_password);
      return;
    }
    surface.proceedToNextTask();
  }

  private void onAccessPointClicked(ScannedAccessPoint accessPoint) {
    surface.updateInputSsid(accessPoint.getSsid());
  }

  public interface Surface extends BaseController.Surface {
    void initListAdapter(Observer<ScannedAccessPoint> ssidClickObserver);

    void showSsids(List<ScannedAccessPoint> accessPoints);

    void updateInputSsid(String ssid);

    void showSsidError(@StringRes int errorMsg);

    void showPasswordError(@StringRes int errorMsg);

    void clearErrors();

    void proceedToNextTask();
  }

}
