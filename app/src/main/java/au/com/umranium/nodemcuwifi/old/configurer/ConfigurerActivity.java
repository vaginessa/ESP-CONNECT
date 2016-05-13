package au.com.umranium.nodemcuwifi.old.configurer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.old.configurer.ConfigurerFragment.SsidProvider;

public class ConfigurerActivity extends AppCompatActivity implements SsidProvider {

  private static final String KEY_HOTSPOT_SSID = "hotspot_ssid";

  public static Intent newIntent(Context context, String hotSpotSsid) {
    Intent intent = new Intent(context, ConfigurerActivity.class);
    intent.putExtra(KEY_HOTSPOT_SSID, hotSpotSsid);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_configurer);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  @Override
  public String getSsid() {
    return getIntent().getStringExtra(KEY_HOTSPOT_SSID);
  }
}
