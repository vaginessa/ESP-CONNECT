package au.com.umranium.espconnect.app.taskscreens.configuring.viewstate;

import au.com.umranium.espconnect.app.common.data.ConfigDetails;
import au.com.umranium.espconnect.app.taskscreens.configuring.ConfiguringController;

public class ShowDone extends UpdateViewState {

  private final ConfigDetails configDetails;

  public ShowDone(ConfigDetails configDetails) {
    this.configDetails = configDetails;
  }

  @Override
  public void apply(ConfiguringController.Surface surface) {
    surface.proceedToNextTask(configDetails.getSsid());
  }

}
