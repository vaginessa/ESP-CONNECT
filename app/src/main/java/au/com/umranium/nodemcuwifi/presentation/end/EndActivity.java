package au.com.umranium.nodemcuwifi.presentation.end;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import au.com.umranium.nodemcuwifi.R;

/**
 * The activity that displays the end message.
 */
public class EndActivity extends AppCompatActivity {

  @NonNull
  public static Intent createIntent(@NonNull Context context) {
    return new Intent(context, EndActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_end);
    setResult(RESULT_OK);
  }

}
