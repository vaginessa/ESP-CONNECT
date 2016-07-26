package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import javax.inject.Inject;
import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.presentation.common.ConfigDetails;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Controller for the configuring task screen.
 */
public class ConfiguringController extends BaseTaskController<ConfiguringController.Surface> {

  private final WifiConnectionUtil wifiConnectionUtil;
  private final ConfigDetails configDetails;
  private final NodeMcuService service;
  private final Scheduler scheduler;
  private Subscription task;

  @Inject
  public ConfiguringController(Surface surface, WifiConnectionUtil wifiConnectionUtil, ConfigDetails configDetails, NodeMcuService service, Scheduler scheduler) {
    super(surface);
    this.wifiConnectionUtil = wifiConnectionUtil;
    this.configDetails = configDetails;
    this.service = service;
    this.scheduler = scheduler;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    surface.setTitle(R.string.configuring_title);
    surface.setMessage(R.string.configuring_description);
  }

  @Override
  public void onStart() {
    if (!wifiConnectionUtil.isAlreadyConnected()) {
      surface.cancelTask();
    }

    if (task != null) {
      task.unsubscribe();
    }

    task = service
        .save(configDetails.getSsid(), configDetails.getPassword())
        .subscribeOn(scheduler.io())
        .observeOn(scheduler.mainThread())
        .subscribe(new Action1<Void>() {
          @Override
          public void call(Void aVoid) {
            surface.proceedToNextTask();
          }
        });
  }

  @Override
  public void onStop() {
    if (task != null) {
      task.unsubscribe();
      task = null;
    }
  }

  public interface Surface extends BaseTaskController.Surface {
    void proceedToNextTask();
  }
}
