package au.com.umranium.nodemcuwifi.presentation.welcome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.task.scanning.ScanningActivity;

/**
 * The activity that displays the welcoming message.
 */
public class WelcomeActivity extends BaseActivity implements WelcomeController.Surface {

  @NonNull
  @Override
  protected BaseController createController() {
    return new WelcomeController(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_welcome);

    Button btnStart = (Button) findViewById(R.id.btn_start);

    assert btnStart != null;

    btnStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((WelcomeController) controller).onStartBtnClicked();
      }
    });
  }

  @Override
  public void proceedToNextScreen() {
    Intent intent = ScanningActivity.createIntent(this);
    startNextActivity(intent);
  }
}
