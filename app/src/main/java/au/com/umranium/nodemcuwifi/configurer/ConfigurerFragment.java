package au.com.umranium.nodemcuwifi.configurer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import au.com.umranium.nodemcuwifi.R;
import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.NodeMcuService.Builder;
import au.com.umranium.nodemcuwifi.api.State;
import au.com.umranium.nodemcuwifi.configurer.utils.rx.IsConnectedToNetwork;
import au.com.umranium.nodemcuwifi.configurer.utils.rx.ValidNetworkInfo;
import au.com.umranium.nodemcuwifi.utils.rx.ObservableValue;
import au.com.umranium.nodemcuwifi.utils.rx.ToInstance;
import au.com.umranium.nodemcuwifi.utils.rx.ToVoid;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
@SuppressLint("CustomError")
public class ConfigurerFragment extends Fragment {

    private static final String TAG = ConfigurerFragment.class.getSimpleName();

    private static final long DURATION_BEFORE_RECONNECT_MS = TimeUnit.SECONDS.toMillis(10);

    private final ObservableValue<NodeMcuService> mNodeMcuService = new ObservableValue<>(true);
    private String mQuotedSsid;
    private WifiController mWifiController;
    private Subscription mReconnectToWifi;
    private Subscription mInitialiseNodeMCUService;
    private Subscription mLoadStateData;

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
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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

        mWifiController = new WifiController(wifiManager, connectivityManager, mQuotedSsid);
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean connected = mWifiController.isAlreadyConnected();

        if (mReconnectToWifi != null) {
            mReconnectToWifi.unsubscribe();
        }

        // Reconnects to the node MCU access point if DURATION_BEFORE_RECONNECT_MS ms go by after a
        // disconnect or connecting to another network
        mReconnectToWifi = Observable
                .merge(
                        // emit a true (reconnect) if disconnected
                        WifiEvents
                                .getInstance()
                                .getDisconnected()
                                .map(ToInstance.getInstance(true)),
                        // emit a false (no reconnect) if connected to the NodeMCU
                        //  (to cancel any recent true) or true (reconnect) if connected to
                        //  another network.
                        WifiEvents
                                .getInstance()
                                .getConnected()
                                .filter(new ValidNetworkInfo())
                                .map(new IsConnectedToNetwork(mQuotedSsid).negate())
                )
                        // ensure that only true (reconnect) values that last at least
                        //  DURATION_BEFORE_RECONNECT_MS milliseconds get acted upon
                .debounce(DURATION_BEFORE_RECONNECT_MS, TimeUnit.MILLISECONDS)
                        // inject a reconnect event if initially not connected
                .startWith(!connected ? Observable.just(true) : Observable.<Boolean>empty())
                        // act on events
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean reconnect) {
                        if (reconnect) {
                            try {
                                mWifiController.connectToNetwork();
                            } catch (WifiConnectionException e) {
                                showError(e.getMessageId());
                                Log.e(TAG, getString(e.getMessageId()), e);
                            }
                        }
                    }
                });

        // Initialises the NodeMCU service after connecting to the NodeMCU access point
        mInitialiseNodeMCUService = WifiEvents
                .getInstance()
                .getConnected()
                .filter(new ValidNetworkInfo())
                .filter(new IsConnectedToNetwork(mQuotedSsid))
                .map(ToVoid.getInstance())
                        // inject an initial event if already connected
                .startWith(connected ? Observable.<Void>just(null) : Observable.<Void>empty())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        initNodeMcuService();
                    }
                });

        // Queries the NodeMCU state information as soon as the NodeMCU service is established
        mLoadStateData = mNodeMcuService
                .getNonNullObservable()
                .observeOn(Schedulers.io())
                .flatMap(new Func1<NodeMcuService, Observable<State>>() {
                    @Override
                    public Observable<State> call(NodeMcuService nodeMcuService) {
                        return nodeMcuService.getState();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<State>() {
                    @Override
                    public void call(State state) {
                        Log.d(TAG, state.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        Log.e(TAG, error.getMessage(), error);
                        showError(error.getMessage());
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
        if (mInitialiseNodeMCUService != null) {
            mInitialiseNodeMCUService.unsubscribe();
            mInitialiseNodeMCUService = null;
        }
        if (mLoadStateData != null) {
            mLoadStateData.unsubscribe();
            mLoadStateData = null;
        }
        //TODO: Reconnect back to original network
    }

    private void initNodeMcuService() {
        Builder builder = new Builder(mWifiController.getGateway());
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Network network = mWifiController.getWifiNetwork();
            builder.setSocketFactory(network.getSocketFactory());
        }
        mNodeMcuService.setValue(builder.build());
    }

    private void showError(@StringRes int msg) {
        //noinspection ConstantConditions
        final Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void showError(String msg) {
        //noinspection ConstantConditions
        final Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    interface SsidProvider {
        String getSsid();
    }

}
