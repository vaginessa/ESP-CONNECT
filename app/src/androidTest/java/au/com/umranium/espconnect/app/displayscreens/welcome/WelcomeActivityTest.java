package au.com.umranium.espconnect.app.displayscreens.welcome;

import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import au.com.umranium.espconnect.R;
import au.com.umranium.espconnect.app.taskscreens.scanning.ScanningActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

  @Rule
  public ActivityTestRule<WelcomeActivity> activityRule = new ActivityTestRule<>(WelcomeActivity.class);


  @Test
  public void whenStartButtonIsClicked_proceedsToScanningActivity() {
    Intents.init();
    onView(withId(R.id.btn_start))
      .perform(click());
    intended(hasComponent(ScanningActivity.class.getName()));
  }

}
