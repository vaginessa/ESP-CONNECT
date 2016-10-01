package au.com.umranium.espconnect.app.taskscreens.configuring.viewstate;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.app.common.StringProvider;

public class ShowSavingCredentials extends UpdateViewMessage {

  public ShowSavingCredentials(StringProvider stringProvider) {
    super(stringProvider.getString(R.string.configuring_esp_save));
  }

}
