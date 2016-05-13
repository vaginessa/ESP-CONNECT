package au.com.umranium.nodemcuwifi.presentation.task.scanning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.task.BaseTaskActivity;

/**
 * An activity that scans for ESP8266 nodes.
 */
public class ScanningActivity extends BaseTaskActivity {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ScanningActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initUi(R.string.scanning_title, R.string.scanning_description);
  }

  @Override
  protected void proceed() {

  }
}
