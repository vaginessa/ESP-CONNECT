package au.com.umranium.espconnect.presentation.tasks.scanning;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import au.com.umranium.espconnect.presentation.common.ScannedAccessPoint;
import au.com.umranium.espconnect.utils.BootTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Extracts the results of scanned access points.
 */
public class ScannedAccessPointExtractor {

  private final WifiManager wifiManager;

  @Inject
  public ScannedAccessPointExtractor(WifiManager wifiManager) {
    this.wifiManager = wifiManager;
  }

  public List<ScannedAccessPoint> extract() {
    List<ScannedAccessPoint> accessPoints = new ArrayList<>();

    for (ScanResult scanResult : wifiManager.getScanResults()) {
      String name = scanResult.SSID;
      int level = WifiManager.calculateSignalLevel(scanResult.level, 100);
      accessPoints.add(new ScannedAccessPoint(scanResult.BSSID, name, level));
    }

    return accessPoints;
  }

}
