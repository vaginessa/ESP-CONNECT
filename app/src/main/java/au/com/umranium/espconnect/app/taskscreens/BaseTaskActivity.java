package au.com.umranium.espconnect.app.taskscreens;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.app.BaseController;
import au.com.umranium.espconnect.app.common.views.ProgressIndicator;
import au.com.umranium.espconnect.app.displayscreens.error.ErrorActivity;

/**
 * A generic activity that performs a long running task.
 */
abstract public class BaseTaskActivity<BaseControllerType extends BaseController> extends BaseActivity<BaseControllerType> {

  private TextView txtTitle;
  private TextView txtDescription;
  private ProgressIndicator progressIndicator;

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_base_task);
    txtTitle = (TextView) findViewById(R.id.txt_title);
    assert txtTitle != null;
    txtDescription = (TextView) findViewById(R.id.txt_description);
    assert txtDescription != null;
    progressIndicator = (ProgressIndicator) findViewById(R.id.progress_indicator);
    assert progressIndicator != null;
  }

  public void setTitle(@StringRes int title) {
    txtTitle.setText(title);
  }

  public void setTitle(String title) {
    txtTitle.setText(title);
  }

  public void setMessage(@StringRes int message) {
    txtDescription.setText(message);
  }

  public void setMessage(String message) {
    txtDescription.setText(message);
  }

  public void setProgressCurrentStep(int step) {
    progressIndicator.setCurrentStep(step);
  }

  public void showErrorScreen(@StringRes int title, @StringRes int message) {
    Intent intent = ErrorActivity.createIntent(this, getString(title), getString(message));
    startNextActivity(intent);
  }

  public void showErrorScreen(String title, String message) {
    Intent intent = ErrorActivity.createIntent(this, title, message);
    startNextActivity(intent);
  }

  public void setAnimation(@DrawableRes int animationRes) {
    ImageView animationImage = (ImageView) findViewById(R.id.img_animation);
    animationImage.setImageResource(animationRes);
    AnimationDrawable animationDrawable = (AnimationDrawable) animationImage.getDrawable();
    animationDrawable.start();
  }

}
