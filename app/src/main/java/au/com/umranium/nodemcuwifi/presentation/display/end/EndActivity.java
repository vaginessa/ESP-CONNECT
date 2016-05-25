package au.com.umranium.nodemcuwifi.presentation.display.end;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;
import au.com.umranium.nodemcuwifi.presentation.display.error.DaggerErrorComponent;
import au.com.umranium.nodemcuwifi.presentation.display.error.ErrorModule;

/**
 * The activity that displays the end message.
 */
public class EndActivity extends BaseActivity<EndController> implements EndController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, EndActivity.class);
  }

  @Override
  protected void doInjection() {
    DaggerEndComponent
        .builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .endModule(new EndModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_end);
    setResult(RESULT_OK);
  }
}
