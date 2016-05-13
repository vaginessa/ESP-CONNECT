package au.com.umranium.nodemcuwifi.presentation.task;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import au.com.umranium.nodemcuwifi.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

/**
 * A generic activity that performs a long running task.
 */
abstract public class BaseTaskActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base_task);

    // TODO: Remove this
    Observable
        .timer(2, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Long>() {
          @Override
          public void call(Long aLong) {
            proceed();
          }
        });
  }

  protected void initUi(@StringRes int title, @StringRes int description) {
    TextView txtTitle = (TextView) findViewById(R.id.txt_title);
    TextView txtDescription = (TextView) findViewById(R.id.txt_description);

    assert txtTitle != null;
    assert txtDescription != null;

    txtTitle.setText(title);
    txtDescription.setText(description);
  }

  abstract protected void proceed();

}
