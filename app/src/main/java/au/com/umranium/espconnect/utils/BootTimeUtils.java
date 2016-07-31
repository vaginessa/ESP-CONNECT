package au.com.umranium.espconnect.utils;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

public class BootTimeUtils {

  @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
  public static long getDiffBootFromUtc() {
    return System.currentTimeMillis() -
        TimeUnit.NANOSECONDS.toMillis(SystemClock.elapsedRealtimeNanos());
  }

  public static long timeSinceBootToUtc(long timeSinceBoot, long diffBootFromUtc) {
    return timeSinceBoot + diffBootFromUtc;
  }

  public static long utcToTimeSinceBoot(long utcTime, long diffBootFromUtc) {
    return utcTime + diffBootFromUtc;
  }
}