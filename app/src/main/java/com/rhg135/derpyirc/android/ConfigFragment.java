package com.rhg135.derpyirc.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by rhg135 on 03/03/15.
 */
public class ConfigFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView =
                new ListView(getActivity());
        rootView.setId(android.R.id.list);
        return rootView;
    }
}
