package com.rhg135.derpyirc.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.Persistents;
import com.rhg135.derpyirc.core.AtomicState;
import com.rhg135.derpyirc.core.Core;
import com.rhg135.derpyirc.core.Macros;
import com.rhg135.derpyirc.core.Options;

import java.lang.reflect.Constructor;
import java.util.HashSet;

/**
 * Created by rhg135 on 28/02/15.
 */
public class CoreFragment extends Fragment {
    public static final String LOG_TAG = "DerpyIRCCore";
    protected final AtomicState<PersistentMap<String, Object>> state = new AtomicState<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_irc, container, false);

        PersistentMap<String, Object> newState;

        // state
        newState = Persistents.arrayMap();

        // load macros
        newState = Macros.loadDebugMacros(newState.plus("macros", Persistents.hashMap()));

        // set local state
        state.set(newState);
        Log.d(LOG_TAG, "Set new state to: " + newState.toString());

        // preferences
        // NOTE: is this the correct Context?
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // edit
        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
        // TODO: this is bad--very bad
        final Object imeOpts = prefs.getInt(String.valueOf(Options.IME_OPTIONS), EditorInfo.IME_ACTION_SEND);
        /* if (imeOpts == null) {
            imeOpts = EditorInfo.IME_ACTION_DONE;
            parent.setKey(imeOptsKey, imeOpts);
        } */
        editText.setImeOptions((int) imeOpts);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onSubmit(editText);
                    handled = true;
                }
                return handled;
            }
        });
        final View sendBtn = rootView.findViewById(R.id.button);
        if (prefs.getBoolean(String.valueOf(Options.BUTTON_ENABLED), true)) {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSubmit(editText);
                }
            });
        } else {
            sendBtn.setVisibility(View.GONE);
        }

        // plugins
        for (String plugin : prefs.getStringSet(String.valueOf(Options.AUTOLOAD_PLUGINS), new HashSet())) {
            try {
                Class klass = Class.forName(plugin);
                Constructor constructor = klass.getConstructor(PersistentMap.class);
                constructor.newInstance(state);
            } catch (NoSuchMethodException e) {
                Log.e(LOG_TAG, "Plugin: " + plugin + " has invalid constructor", e);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Plugin: " + plugin + " failed to load.", e);
            }
        }
        return rootView;
    }
    public void onSubmit(EditText v) {
        // TODO: do stuff
        final Editable text = v.getText();
        Log.d(LOG_TAG, "Text: " + text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Core.dispatch(state, text.toString());
            }
        }).start();

    }
}