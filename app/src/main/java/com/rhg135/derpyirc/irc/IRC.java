package com.rhg135.derpyirc.irc;

import com.github.krukow.clj_ds.PersistentMap;

import java.util.Map;

import static com.rhg135.derpyirc.core.Useful.plusIfNone;

/**
 * Created by rhg135 on 03/03/15.
 */
public class IRC {
    public IRC(Map<String, PersistentMap> globalState) {
        final PersistentMap<String, String> config = globalState.get("config");
        final PersistentMap<String, String> newConfig =
                plusIfNone(config, "irc.version", "Derpy IRC 0.0.1");
        globalState.put("config", newConfig);
    }
}
