package au.com.umranium.nodemcuwifi.configurer.utils.rx;

import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;

import au.com.umranium.nodemcuwifi.utils.rx.Pred;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnected;

/**
 * Sometimes after the phone disconnects from one network and before it connects to another network
 * it shows that it's connected to a network with a network id of -1. This class returns true if the
 * connected network has a valid network id and the supplicant state is set to {@link
 * SupplicantState#COMPLETED}.
 *
 * @author umran
 */
public class ValidNetworkInfo extends Pred<WifiConnected> {
    @Override
    public Boolean call(WifiConnected connected) {
        WifiInfo wifiInfo = connected.getWifiInfo();
        return wifiInfo.getNetworkId() > 0 &&
                SupplicantState.COMPLETED.equals(wifiInfo.getSupplicantState());
    }
}
