package com.rhg135.derpyirc.core;

import org.slf4j.LoggerFactory;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class StateMacro<S> implements IMacro<S> {
    @Override
    public void macro(S globalState, String[] commandParts) {
        LoggerFactory.getLogger(StateMacro.class).debug("State: " + globalState.toString());
    }
}
