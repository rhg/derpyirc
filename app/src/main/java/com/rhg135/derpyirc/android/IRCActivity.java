package com.rhg135.derpyirc.android;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;


public class IRCActivity extends ActionBarActivity {
    protected final Map<String, Fragment> fragments = new HashMap<>();
    public static final String LOG_TAG = "DerpyIRCMain";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_irc);

        //preload fragments
        fragments.put("core", new CoreFragment());
        fragments.put("config", new ConfigFragment());

        // I think I know how to thread
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        if (bundle == null) {
            // android fragments
            Log.d(LOG_TAG, "adding CoreFragment");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragments.get("core"))
                    .commit();
        }
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
            final Fragment configFragment = fragments.get("config");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, configFragment)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
