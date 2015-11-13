package maimeng.yodian.app.client.android2.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android2.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainTabActivityFragment extends Fragment {

    public MainTabActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_tab, container, false);
    }
}
