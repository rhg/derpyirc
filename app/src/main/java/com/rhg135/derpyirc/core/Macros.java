package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class Macros {

    public static final Logger logger = LoggerFactory.getLogger(Macros.class);

    public static PersistentMap<String, Object> loadDebugMacros(PersistentMap<String, Object> globalState) {
        final PersistentMap<String, IMacro<Map>> macrosMap =
                ((PersistentMap<String, IMacro<Map>>) globalState.get("macros"))
                        .plus("state", new StateMacro());
        return globalState.plus("macros", macrosMap);
    }

    public final static class StateMacro implements IMacro<Map> {
        @Override
        public void macro(Map globalState, String[] commandParts) {
            LoggerFactory.getLogger(StateMacro.class).debug("State: " + globalState.toString());
            final Map io = (Map) globalState.get("io");
            final SynchronousQueue<String> display = (SynchronousQueue<String>) io.get("display");
            try {
                display.put(globalState.toString());
            } catch (InterruptedException e) {
                Macros.logger.error("Macro Interrupted", e);
            }
        }
    }

}
