package au.com.umranium.nodemcuwifi.presentation.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * A collection of helper functions for handling intent extras.
 *
 * @author umran
 */
public final class IntentExtras {

  /**
   * Gets and returns the intent extra identified by the key given from the activity given
   * or throws an exception.
   *
   * @param activity Activity from which to extract the intent extra from
   * @param key      Key of the extra to extract
   * @return extra value from the activity's intent
   */
  public static int getIntExtra(Activity activity, String key) {
    Intent intent = activity.getIntent();
    if (!intent.hasExtra(key)) {
      throw new RuntimeException(activity.getClass().getSimpleName() +
          " was initiated without the parameter '" + key + "'");
    }
    return intent.getIntExtra(key, -1);
  }

}
