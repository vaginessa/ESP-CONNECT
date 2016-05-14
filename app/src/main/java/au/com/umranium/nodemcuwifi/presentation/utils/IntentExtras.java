package au.com.umranium.nodemcuwifi.presentation.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

/**
 * A collection of helper functions for handling intent extras.
 *
 * @author umran
 */
public final class IntentExtras {

  public static class ParamNotFoundException extends RuntimeException {
    public ParamNotFoundException(Activity activity, String key) {
      super(activity.getClass().getSimpleName() + " was initiated without the parameter '" + key + "'");
    }
  }

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
      throw new ParamNotFoundException(activity, key);
    }
    return intent.getIntExtra(key, -1);
  }

  /**
   * Gets and returns the intent extra identified by the key given from the activity given
   * or throws an exception.
   *
   * @param activity Activity from which to extract the intent extra from
   * @param key      Key of the extra to extract
   * @return extra value from the activity's intent
   */
  public static Parcelable[] getParcelableArrayExtra(Activity activity, String key) {
    Intent intent = activity.getIntent();
    if (!intent.hasExtra(key)) {
      throw new ParamNotFoundException(activity, key);
    }
    return intent.getParcelableArrayExtra(key);
  }

  /**
   * Gets and returns the intent extra identified by the key given from the activity given
   * or throws an exception.
   *
   * @param activity Activity from which to extract the intent extra from
   * @param key      Key of the extra to extract
   * @return extra value from the activity's intent
   */
  public static <T extends Parcelable> T getParcelableExtra(Activity activity, String key) {
    Intent intent = activity.getIntent();
    if (!intent.hasExtra(key)) {
      throw new ParamNotFoundException(activity, key);
    }
    return intent.getParcelableExtra(key);
  }

}
