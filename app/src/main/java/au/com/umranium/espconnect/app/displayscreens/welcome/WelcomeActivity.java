package au.com.umranium.espconnect.app.displayscreens.welcome;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.di.activity.ActivityModule;
import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.app.taskscreens.scanning.ScanningActivity;

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

    TextView moreInfo = (TextView) findViewById(R.id.txt_link_to_more_info_about_app);
    assert moreInfo != null;
    moreInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        controller.onMoreAppInfoTxtClicked();
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
