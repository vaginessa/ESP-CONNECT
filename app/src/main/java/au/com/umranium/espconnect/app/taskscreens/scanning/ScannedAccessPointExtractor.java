package au.com.umranium.espconnect.app.taskscreens.scanning;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.umranium.espconnect.app.common.data.ScannedAccessPoint;

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
