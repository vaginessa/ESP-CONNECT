package au.com.umranium.nodemcuwifi.configurer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.utils.rx.ToInstance;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnectivityEvent;
import au.com.umranium.nodemcuwifi.wifievents.WifiDisconnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A placeholder fragment containing a simple view.
 */
@SuppressLint("CustomError")
public class ConfigurerFragment extends Fragment {

    private static final String TAG = ConfigurerFragment.class.getSimpleName();

    private static final long DURATION_BEFORE_RECONNECT_MS = TimeUnit.SECONDS.toMillis(10);

    private WifiManager mWifiManager;
    private Subscription mConnectToWifi;
    private Subscription mReconnectToWifi;
    private String mQuotedSsid;

    public ConfigurerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configurer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        Activity activity = getActivity();
        String ssid;
        if (activity instanceof SsidProvider) {
            SsidProvider ssidProvider = (SsidProvider) activity;
            ssid = ssidProvider.getSsid();

            if (ssid == null) {
                throw new RuntimeException("SSID not provided for fragment");
            }
        } else {
            throw new RuntimeException("Fragment activity does not provide the SSID value");
        }

        mQuotedSsid = "\"" + ssid + "\"";

        mConnectToWifi = WifiEvents
                .getInstance()
                .getConnectivityEvents()
                .subscribe(new Action1<WifiConnectivityEvent>() {
                    @Override
                    public void call(WifiConnectivityEvent event) {
                        if (event instanceof WifiConnected) {
                            WifiConnected connected = (WifiConnected) event;
                            WifiInfo wifiInfo = connected.getWifiInfo();
                            showMsg("Connected to " + wifiInfo.getSSID() + "(" + wifiInfo
                                    .getNetworkId() + ") " + wifiInfo.getBSSID() + " " +
                                    wifiInfo.getSupplicantState());
                        } else if (event instanceof WifiDisconnected) {
                            showMsg("Wifi disconnected");
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isAlreadyConnected()) {
            connectToNetwork();
        } else {
            showMsg("Already connected to " + mQuotedSsid);
        }

        if (mReconnectToWifi != null) {
            mReconnectToWifi.unsubscribe();
        }
        // Reconnects to network if DURATION_BEFORE_RECONNECT_MS ms go by after a disconnect
        //  or connecting to another network
        mReconnectToWifi = Observable
                .merge(
                        WifiEvents
                                .getInstance()
                                .getDisconnected()
                                .map(ToInstance.getInstance(true)),
                        WifiEvents
                                .getInstance()
                                .getConnected()
                                .filter(new ValidNetworkInfo())
                                .map(new IsNotConnectedToNetwork(mQuotedSsid))
                )
                .debounce(DURATION_BEFORE_RECONNECT_MS, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean reconnect) {
                        if (reconnect) {
                            connectToNetwork();
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mReconnectToWifi != null) {
            mReconnectToWifi.unsubscribe();
            mReconnectToWifi = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mConnectToWifi != null) {
            mConnectToWifi.unsubscribe();
            mConnectToWifi = null;
        }
    }

    private WifiConfiguration getConfiguredNetwork() {
        for (WifiConfiguration conf : mWifiManager.getConfiguredNetworks()) {
            if (conf.SSID != null && mQuotedSsid.equals(conf.SSID)) {
                return conf;
            }
        }
        return null;
    }

    private boolean isAlreadyConnected() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo != null && mQuotedSsid.equals(wifiInfo.getSSID());
    }

    private void connectToNetwork() {
        WifiConfiguration wifiConf = getConfiguredNetwork();

        int netId;
        if (wifiConf != null) {
            showMsg("Network already added");
            netId = wifiConf.networkId;
        } else {
            netId = addNetwork();
            if (netId < 0) {
                // add network failed
                showMsg("Unable to add the wifi network");
            } else {
                showMsg("Added and enabled network");
            }
        }

        showMsg(" Connect to network: " + netId);

        if (!mWifiManager.disconnect()) {
            throw new RuntimeException("Unable to disconnect");
        }
        if (!mWifiManager.enableNetwork(netId, true)) {
            throw new RuntimeException("Unable to enable network");
        }
    }

    private int addNetwork() {
        WifiConfiguration wifiConf = new WifiConfiguration();
        wifiConf.SSID = mQuotedSsid;
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        return mWifiManager.addNetwork(wifiConf);
    }

    private static void showMsg(String msg) {
        Log.d(TAG, msg);
    }

    interface SsidProvider {
        String getSsid();
    }

    private static class ValidNetworkInfo implements Func1<WifiConnected, Boolean> {
        @Override
        public Boolean call(WifiConnected connected) {
            return connected.getWifiInfo() != null &&
                    connected.getWifiInfo().getNetworkId() > 0 &&
                    SupplicantState.COMPLETED.equals(connected.getWifiInfo().getSupplicantState());
        }
    }

    private static class IsNotConnectedToNetwork implements Func1<WifiConnected, Boolean> {
        private final String mQuotedSsid;

        public IsNotConnectedToNetwork(String quotedSsid) {
            mQuotedSsid = quotedSsid;
        }

        @Override
        public Boolean call(WifiConnected connected) {
            return !mQuotedSsid.equals(connected.getWifiInfo().getSSID());
        }
    }
}
