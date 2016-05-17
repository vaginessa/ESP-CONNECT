package au.com.umranium.nodemcuwifi.presentation.display.end;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.presentation.common.BaseActivity;
import au.com.umranium.nodemcuwifi.presentation.common.BaseController;

/**
 * The activity that displays the end message.
 */
public class EndActivity extends BaseActivity implements EndController.Surface {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, EndActivity.class);
  }

  @NonNull
  @Override
  protected BaseController createController() {
    return new EndController(this);
  }

  @Override
  protected void initUi() {
    setContentView(R.layout.activity_end);
    setResult(RESULT_OK);
  }
}
