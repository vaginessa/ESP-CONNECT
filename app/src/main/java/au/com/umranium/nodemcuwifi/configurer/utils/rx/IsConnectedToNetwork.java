package au.com.umranium.nodemcuwifi.configurer.utils.rx;

import au.com.umranium.nodemcuwifi.utils.rx.Pred;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnected;

/**
 * Returns true if connected to a network of the given SSID. The SSID given should have quoted (e.g.
 * "MyAccessPoint").
 *
 * @author umran
 */
public class IsConnectedToNetwork extends Pred<WifiConnected> {
    private final String mQuotedSsid;

    public IsConnectedToNetwork(String quotedSsid) {
        mQuotedSsid = quotedSsid;
    }

    @Override
    public Boolean call(WifiConnected connected) {
        return mQuotedSsid.equals(connected.getWifiInfo().getSSID());
    }
}
