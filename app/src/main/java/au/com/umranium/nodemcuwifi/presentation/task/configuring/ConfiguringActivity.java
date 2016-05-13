package au.com.umranium.nodemcuwifi.presentation.task.configuring;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.task.BaseTaskActivity;

/**
 * An activity that configures an ESP8266 node.
 */
public class ConfiguringActivity extends BaseTaskActivity {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ConfiguringActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initUi(R.string.configuring_title, R.string.configuring_description);
  }

}
