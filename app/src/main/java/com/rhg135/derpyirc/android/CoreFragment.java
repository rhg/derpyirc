package com.rhg135.derpyirc.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.Persistents;

import java.util.concurrent.atomic.AtomicReference;

import static com.rhg135.derpyirc.android.Utils.get;

/**
 * Created by rhg135 on 28/02/15.
 */
public class CoreFragment extends Fragment {
   EditText editText;
    public static final String LOG_TAG = "DerpyIRCCore";
    protected final AtomicReference<PersistentMap<String, Object>> stateRef = new AtomicReference<>(null);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_irc, container, false);

        PersistentMap<String, Object> newState;

        // persistence
        if (bundle == null) {
            // default new state
            newState = Persistents.arrayMap();
        } else {
            // TODO: implement restoring state
            newState = Persistents.arrayMap();
            /* PersistentMap<String, Object> savedState = Utils.read(bundle, "state");
            if (savedState == null) {
                savedState = Persistents.arrayMap();
            }
            state.set(savedState); */
        }
        // set local state
        stateRef.set(newState);
        Log.d(LOG_TAG, "Set new state to: " + newState.toString());

        // edit
        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
        final String imeOptsKey = "config.core.field.ime.options";
        // TODO: this is bad--very bad
        final Object imeOpts = get(getState(), imeOptsKey, EditorInfo.IME_ACTION_DONE);
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
        ((Button) rootView.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit(editText);
            }
        });
        this.editText = editText;

        return rootView;
    }

    public PersistentMap<String, Object> getState() {
        return stateRef.get();
    }

    private void setKey(String key, PersistentMap<String, ?> obj) {
        PersistentMap<String, Object> oldState;
        boolean set = false;
        do {
            oldState = stateRef.get();
            set = stateRef.compareAndSet(oldState, oldState.plus(key, obj));
        } while (!set);
    }
    public void connect(long serverID) {
        // TODO: stuff
    }
    public void onSubmit(EditText v) {
        // TODO: do stuff
        final Editable text = v.getText();
        Log.d(LOG_TAG, "Text: " + text);
        final String[] split = text.toString().split("\\s+");
        Log.d(LOG_TAG, "Parsed: " + split);
    }
}