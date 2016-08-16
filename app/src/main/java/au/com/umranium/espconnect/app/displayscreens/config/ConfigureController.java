package au.com.umranium.espconnect.app.displayscreens.config;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.analytics.ScreenTracker;
import au.com.umranium.espconnect.app.BaseController;
import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;
import au.com.umranium.espconnect.app.taskscreens.utils.WifiConnectionUtil;
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
  private final WifiConnectionUtil wifiConnectionUtil;

  @Inject
  public ConfigureController(Surface surface,
                             ScreenTracker screenTracker,
                             ScannedAccessPoint accessPoint,
                             List<ScannedAccessPoint> ssids,
                             WifiConnectionUtil wifiConnectionUtil) {
    super(surface, screenTracker);
    this.accessPoint = accessPoint;
    this.ssids = ssids;
    this.wifiConnectionUtil = wifiConnectionUtil;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    screenTracker.startConfigure();

    surface.initListAdapter(accessPointClickEvents);

    accessPointClickEvents.subscribe(new Action1<ScannedAccessPoint>() {
      @Override
      public void call(@NonNull ScannedAccessPoint accessPoint) {
        onAccessPointClicked(accessPoint);
      }
    });

    surface.showSsids(ssids);
  }

  @Override
  public void onStart() {
    // TODO: Change to listening broadcasted wifi events
    if (!wifiConnectionUtil.isAlreadyConnected()) {
      surface.cancelTask();
    }
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
    surface.proceedToNextTask(new ConfigDetails(ssid, password));
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

    void proceedToNextTask(ConfigDetails configDetails);
  }

}
