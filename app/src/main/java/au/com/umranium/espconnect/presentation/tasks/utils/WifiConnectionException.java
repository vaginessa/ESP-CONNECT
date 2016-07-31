package au.com.umranium.espconnect.presentation.tasks.utils;

import android.support.annotation.StringRes;

/**
 * Thrown when an error occurs while attempting to establish a wifi connection.
 *
 * @author umran
 */
public class WifiConnectionException extends Exception {

  @StringRes
  private int mMessageId;

  public WifiConnectionException(@StringRes int messageId) {
    mMessageId = messageId;
  }

  @StringRes
  public int getMessageId() {
    return mMessageId;
  }

}
