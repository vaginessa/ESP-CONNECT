package au.com.umranium.espconnect.app.taskscreens.configuring.viewstate;

import au.com.umranium.espconnect.app.taskscreens.configuring.ConfiguringController;

abstract class UpdateViewMessage extends UpdateViewState {
  private final String message;

  UpdateViewMessage(String message) {
    this.message = message;
  }

  @Override
  public void apply(ConfiguringController.Surface surface) {
    surface.setMessage(message);
  }

}
