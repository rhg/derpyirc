package com.rhg135.derpyirc.core;

import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class StateMacro<S> implements IMacro<S> {
    @Override
    public void macro(S globalState, String[] commandParts) {
        LoggerFactory.getLogger(StateMacro.class).debug("State: " + globalState.toString());
        final Map io = (Map) ((Map) globalState).get("io");
        final SynchronousQueue<String> display = (SynchronousQueue<String>) io.get("display");
        try {
            display.put(globalState.toString());
        } catch (InterruptedException e) {
            Macros.logger.error("Macro Interrupted", e);
        }
    }
}
