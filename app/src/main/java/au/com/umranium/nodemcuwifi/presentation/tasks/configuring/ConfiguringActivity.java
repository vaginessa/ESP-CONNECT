package au.com.umranium.nodemcuwifi.presentation.tasks.configuring;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.display.end.EndActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskActivity;
import au.com.umranium.nodemcuwifi.presentation.tasks.common.BaseTaskController;
import au.com.umranium.nodemcuwifi.presentation.tasks.connecting.ConnectingModule;
import au.com.umranium.nodemcuwifi.presentation.tasks.connecting.DaggerConnectingComponent;

/**
 * An activity that configures an ESP8266 node.
 */
public class ConfiguringActivity extends BaseTaskActivity<ConfiguringController> implements ConfiguringController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, ConfiguringActivity.class);
  }

  @Override
  protected void doInjection() {
    DaggerConfiguringComponent.builder()
        .appComponent(getApp().getAppComponent())
        .activityModule(new ActivityModule(this))
        .configuringModule(new ConfiguringModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected Intent createIntentForNextTask() {
    return EndActivity.createIntent(this);
  }

}
