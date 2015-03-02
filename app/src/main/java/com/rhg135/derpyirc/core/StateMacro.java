package com.rhg135.derpyirc.core;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class StateMacro<S> implements IMacro<S> {
    @Override
    public void macro(S globalState, String[] commandParts) {
        Macros.logger.debug(globalState.toString());
    }
}
