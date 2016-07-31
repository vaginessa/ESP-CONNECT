package au.com.umranium.espconnect.presentation.display.welcome;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.presentation.common.BaseActivity;
import au.com.umranium.espconnect.presentation.tasks.scanning.ScanningActivity;

/**
 * The activity that displays the welcoming message.
 */
public class WelcomeActivity extends BaseActivity<WelcomeController> implements WelcomeController.Surface {

  @Override
  protected void doInjection() {
    DaggerWelcomeComponent
        .builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .welcomeModule(new WelcomeModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_welcome);

    Button btnStart = (Button) findViewById(R.id.btn_start);

    assert btnStart != null;

    btnStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        controller.onStartBtnClicked();
      }
    });
  }

  @Override
  public void proceedToNextScreen() {
    Intent intent = ScanningActivity.createIntent(this);
    startNextActivity(intent);
  }

  @Override
  protected void overrideFinishingTransition() {
    // do nothing
  }
}
