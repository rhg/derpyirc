package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.Persistents;
import com.google.common.base.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class Macros {

    public static final Logger logger = LoggerFactory.getLogger(Macros.class);

    public static Map loadDebugMacros() {
        return Persistents.hashMap()
                        .plus("state", new StateMacro())
                        .plus("set", new SetCommand());
    }

    public final static class SetCommand implements IMacro<Map> {
        @Override
        public void macro(Map globalState, final String[] commandParts) {
            if (commandParts.length == 3) {
                // FIXME: cast
                ((HistoricMap) globalState).swap(new Function<Map, Map>() {
                    @Override
                    public Map apply(Map input) {
                        return Useful.update("config", new Function<Object, Object>() {
                            @Override
                            public Object apply(Object input) {
                                logger.debug(input.getClass().toString());
                                // FIXME: cast
                                return ((PersistentMap) input).plus(commandParts[1], commandParts[2]);
                            }
                        }).apply((PersistentMap<String, Object>) input); // FIXME: cast
                    }
                });
            } else {
                // TODO: factor this bit out, I've done this before
                final Map io = (Map) globalState.get("io");
                // FIXME: cast
                final SynchronousQueue<String> display = (SynchronousQueue<String>) io.get("display");
                try {
                    display.put("Invalid Arguments to SET: " + Arrays.toString(commandParts));
                } catch (InterruptedException e) {
                    logger.error("Who interrupted me?", e);
                }
            }
        }
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
                logger.error("Macro Interrupted", e);
            }
        }
    }

}
