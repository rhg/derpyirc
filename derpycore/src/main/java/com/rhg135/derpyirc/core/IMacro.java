package com.rhg135.derpyirc.core;

/**
 * Created by Ricardo on 3/2/2015.
 */
public interface IMacro<S> {
    public void macro(S globalState, String[] commandParts);
}
