package au.com.umranium.espconnect.app.taskscreens.configuring.viewstate;

import au.com.umranium.espconnect.app.taskscreens.configuring.ConfiguringController;

public class ShowError extends UpdateViewState {

  private final String title;
  private final String message;

  public ShowError(String title, String message) {
    this.title = title;
    this.message = message;
  }

  @Override
  public void apply(ConfiguringController.Surface surface) {
    surface.showErrorScreen(title, message);
  }

}
