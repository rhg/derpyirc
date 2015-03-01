package com.rhg135.derpyirc.android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.Persistents;

import java.util.concurrent.atomic.AtomicReference;


public class IRCActivity extends ActionBarActivity {

    private final AtomicReference<PersistentMap<String, Object>> state = new AtomicReference<PersistentMap<String, Object>>(null);

    public static final String LOG_TAG = "DerpyIRCMain";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_irc);
        PersistentMap<String, Object> newState;

        // persistence
        if (bundle == null) {
            newState = Persistents.arrayMap();

            // android fragments
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CoreFragment())
                    .commit();
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
        state.set(newState);
        Log.d(LOG_TAG, "Set new state to: " + newState.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_irc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public PersistentMap<String, Object> getState() {
        return state.get();
    }

    public void setKey(String key, Object obj) {
        PersistentMap<String, Object> oldState;
        boolean set = false;
        do {
            oldState = state.get();
            set = state.compareAndSet(oldState, oldState.plus(key, obj));
        } while (!set);
    }
}
