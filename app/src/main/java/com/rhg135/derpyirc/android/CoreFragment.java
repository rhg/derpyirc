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
import com.rhg135.derpyirc.core.Core;
import com.rhg135.derpyirc.core.HistoricMap;
import com.rhg135.derpyirc.core.Macros;
import com.rhg135.derpyirc.core.Options;
import com.rhg135.derpyirc.irc.IRC;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by rhg135 on 28/02/15.
 */
public class CoreFragment extends Fragment {
    public static final String LOG_TAG = "DerpyIRCCore";
    protected final Map<String, PersistentMap> state = new HistoricMap();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_irc, container, false);

        // load macros
        state.put("macros", Macros.loadDebugMacros());

        // preferences
        // NOTE: is this the correct Context?
        state.put("config", Persistents.hashMap());
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // output
        final TextView textView = (TextView) rootView.findViewById(R.id.textView);

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

        Log.i(LOG_TAG, "Loading IRC plugin");
        new IRC(state);

        // plugins
        for (String plugin : prefs.getStringSet(String.valueOf(Options.AUTOLOAD_PLUGINS), new HashSet<String>())) {
            try {
                Class klass = Class.forName(plugin);
                Constructor constructor = klass.getConstructor(Map.class);
                constructor.newInstance(state);
            } catch (NoSuchMethodException e) {
                Log.e(LOG_TAG, "Plugin: " + plugin + " has invalid constructor", e);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Plugin: " + plugin + " failed to load.", e);
            }
        }

        // io
        state.put("io", Persistents.hashMap().plus("display", new SynchronousQueue<String>()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final Map io = (Map) state.get("io");
                    final SynchronousQueue<String> display = (SynchronousQueue<String>) io.get("display");
                    try {
                        final String line = display.take();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.append(line + "\n");
                            }
                        });
                    } catch (InterruptedException e) {
                        Log.wtf(LOG_TAG, "What is going on?", e);
                    }
                }
            }
        }).start();


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