package au.com.umranium.espconnect.presentation.utils;

import android.content.res.Resources;

/**
 * A utility to convert DP to Pixels and vice versa.
 */
public class DpUtil {

  private final float dpi;

  public DpUtil(Resources resources) {
    this.dpi = resources.getDisplayMetrics().density;
  }

  public int toPx(int dp) {
    return (int) (dp * dpi);
  }

  public int toDp(int px) {
    return (int) (px / dpi);
  }

}
