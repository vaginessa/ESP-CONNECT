package au.com.umranium.nodemcuwifi.error;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.utils.IntentExtras;

/**
 * An generic activity that displays errors.
 */
public class ErrorActivity extends AppCompatActivity {

  private static final String PARAM_TITLE = "title";
  private static final String PARAM_DESCRIPTION = "description";

  @NonNull
  public static Intent createIntent(@NonNull Context context,
                                    @StringRes int title,
                                    @StringRes int description) {
    Intent intent = new Intent(context, ErrorActivity.class);
    intent.putExtra(PARAM_TITLE, title);
    intent.putExtra(PARAM_DESCRIPTION, description);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_error);

    int title = IntentExtras.getIntExtra(this, PARAM_TITLE);
    int description = IntentExtras.getIntExtra(this, PARAM_DESCRIPTION);

    TextView txtTitle = (TextView) findViewById(R.id.txt_title);
    TextView txtDescription = (TextView) findViewById(R.id.txt_description);
    Button btnOk = (Button) findViewById(R.id.btn_ok);

    assert txtTitle != null;
    assert txtDescription != null;
    assert btnOk != null;

    txtTitle.setText(title);
    txtDescription.setText(description);
    btnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ErrorActivity.this.finish();
      }
    });
  }
}
