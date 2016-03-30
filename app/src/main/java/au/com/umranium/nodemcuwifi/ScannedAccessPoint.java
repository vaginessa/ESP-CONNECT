package au.com.umranium.nodemcuwifi;

/**
 * @author umran
 */
public class ScannedAccessPoint {

    private final long id;
    private final String ssid;
    private final int signalStrength;

    public ScannedAccessPoint(String bssid, String ssid, int signalStrength) {
        this.id = bssidToLong(bssid.toLowerCase());
        this.ssid = ssid;
        this.signalStrength = signalStrength;
    }

    public long getId() {
        return id;
    }

    public String getSsid() {
        return ssid;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    @Override
    public String toString() {
        return ssid;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private static long bssidToLong(String bssid) {
        // ensure that it matches XX:XX:XX:...:XX
        if (!bssid.matches("\\p{XDigit}{2}(?::\\p{XDigit}{2})*")) {
            return -1;
        }

        String[] sections = bssid.split(":");
        long value = 0;
        for (int i = 0; i < sections.length; i++) {
            int sectionValue = Integer.parseInt(sections[i], 16);

            value <<= 8; // move by 1 byte
            value |= sectionValue & 0xFF;
        }

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScannedAccessPoint point = (ScannedAccessPoint) o;

        if (id != point.id) {
            return false;
        }
        return ssid.equals(point.ssid);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + ssid.hashCode();
        return result;
    }
}
