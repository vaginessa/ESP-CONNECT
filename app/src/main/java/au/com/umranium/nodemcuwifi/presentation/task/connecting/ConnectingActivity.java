package au.com.umranium.nodemcuwifi.presentation.task.connecting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.task.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.task.configuring.ConfiguringActivity;

/**
 * An activity that connects to a ESP8266 node.
 */
public class ConnectingActivity extends BaseTaskActivity {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ConnectingActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initUi(R.string.connecting_title, R.string.connecting_description);
  }

  @Override
  protected void proceed() {
    startActivity(ConfiguringActivity.createIntent(this));
    finish();
  }
}
