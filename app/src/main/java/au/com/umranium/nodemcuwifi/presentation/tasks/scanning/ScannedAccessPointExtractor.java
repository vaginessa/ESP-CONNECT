package au.com.umranium.nodemcuwifi.presentation.tasks.scanning;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.utils.BootTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Extracts the results of scanned access points.
 */
public class ScannedAccessPointExtractor {

  private final WifiManager wifiManager;
  private final Pattern nodeAccessPointNameRegex;

  public ScannedAccessPointExtractor(WifiManager wifiManager, String nodeAccessPointNameRegex) {
    this.wifiManager = wifiManager;
    this.nodeAccessPointNameRegex = Pattern.compile(nodeAccessPointNameRegex);
  }

  public List<ScannedAccessPoint> extract(long lastScanRequestTimestamp) {
    long scanRequestBootTimeMicros = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      scanRequestBootTimeMicros = TimeUnit.MILLISECONDS.toMicros(
          BootTimeUtils.utcToTimeSinceBoot(lastScanRequestTimestamp,
              BootTimeUtils.getDiffBootFromUtc()));
    }

    List<ScannedAccessPoint> accessPoints = new ArrayList<>();

    for (ScanResult scanResult : wifiManager.getScanResults()) {
//            Log.d(TAG, "  scan-result: " + scanResult.SSID);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        if (scanResult.timestamp >= scanRequestBootTimeMicros) {
          continue;
        }
      }

      String name = scanResult.SSID;
      if (!nodeAccessPointNameRegex.matcher(name).matches()) {
        continue;
      }

      int level = WifiManager.calculateSignalLevel(scanResult.level, 100);

      accessPoints.add(new ScannedAccessPoint(scanResult.BSSID, name, level));
    }

    return accessPoints;
  }

}
