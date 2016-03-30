package au.com.umranium.nodemcuwifi.configurer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.umranium.nodemcuwifi.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConfigurerFragment extends Fragment {

    public ConfigurerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configurer, container, false);
    }
}
