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

import com.google.common.base.Function;
import com.rhg135.derpyirc.core.Core;
import com.rhg135.derpyirc.core.Options;
import com.rhg135.derpyirc.core.Useful;
import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IState;
import com.rhg135.derpyirc.irc.IRC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by rhg135 on 28/02/15.
 */
public class CoreFragment extends Fragment {
    public static final String LOG_TAG = "DerpyIRCCore";
    protected final ExecutorService pool = Executors.newCachedThreadPool();
    protected final SynchronousQueue queue = new SynchronousQueue();

    public IMap getState() {
        return state.deref();
    }

    protected final IState<IMap> state = Core.newState(new MapProvider(), queue);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_irc, container, false);

        // threading
        Useful.put(state, "pool", pool);

        // preferences
        // TODO: don't use android's; it's hidden state
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
        state.swap(new Function<IMap, IMap>() {
            @Override
            public IMap apply(IMap input) {
                return IRC.loadPlugin(input, pool, new MapProvider());
            }
        });

        /* FIXME: does this even work
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
        */

        // io
        pool.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // TODO: cast
                        final String line = (String) queue.take();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.append(line + "\n");
                            }
                        });
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });

        /*
        pool.submit(new Runnable() {
            @Override
            public void run() {
                Core.dispatch(state, "/join #abc");
                Core.dispatch(state, "/aa51");
                Core.dispatch(state, "/state");
                Core.dispatch(state, "/join #abc");
            }
        }); */

        return rootView;
    }
    public void onSubmit(EditText v) {
        // TODO: do stuff
        final Editable text = v.getText();
        Log.d(LOG_TAG, "Text: " + text);
        pool.submit(new Runnable() {
            @Override
            public void run() {
                Core.dispatch(state, text.toString());
            }
        });

    }
}