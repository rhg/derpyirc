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

    public static PersistentMap loadDebugMacros(PersistentMap globalState) {
        final PersistentMap<String, IMacro<Map>> macrosMap =
                ((PersistentMap<String, IMacro<Map>>) globalState.get("macros"))
                        .plus("state", new StateMacro());
        //      .plus("set", new SetCommand());
        return globalState.plus("macros", macrosMap);
    }

    /*
    public final static class SetCommand implements IMacro<Map> {
        @Override
        public void macro(AtomicState<Map> globalState, final String[] commandParts) {
            if (commandParts.length == 3) {
                globalState.swap(new Function<Map, Map>() {
                    @Override
                    public Map apply(Map input) {
                        return Useful.update("config", new Function<Object, Object>() {
                            @Override
                            public Object apply(Object input) {
                                return ((PersistentMap) input).put(commandParts[1], commandParts[2]);
                            }
                        }).apply((PersistentMap<String, Object>) input);
                    }
                });
            } else {
                final Map io = (Map) globalState.getState().get("io");
                final SynchronousQueue<String> display = (SynchronousQueue<String>) io.get("display");
                try {
                    display.put("Invalid Arguments to SET: " + Arrays.toString(commandParts));
                } catch (InterruptedException e) {
                    logger.error("Who interrupted me?", e);
                }
            }
        }
    }*/

    public final static class StateMacro implements IMacro<Map> {
        @Override
        public void macro(Map globalState, String[] commandParts) {
            LoggerFactory.getLogger(StateMacro.class).debug("State: " + globalState.toString());
            final Map io = (Map) globalState.get("io");
            final SynchronousQueue<String> display = (SynchronousQueue<String>) io.get("display");
            try {
                display.put(globalState.toString());
            } catch (InterruptedException e) {
                logger.error("Macro Interrupted", e);
            }
        }
    }

}
