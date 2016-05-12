package au.com.umranium.nodemcuwifi.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.task.scanning.ScanningActivity;

/**
 * The activity that displays the welcoming message.
 */
public class WelcomeActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    Button btnStart = (Button) findViewById(R.id.btn_start);

    assert btnStart != null;

    btnStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showBusyActivity();
      }
    });
  }

  private void showBusyActivity() {
    Intent intent = ScanningActivity.createIntent(this);
    startActivity(intent);
  }

}
