package com.rhg135.derpyirc.core;

import com.google.common.base.Function;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rhg135 on 01/03/15.
 */
public class AtomicState {
    protected final AtomicReference stateRef = new AtomicReference(null);

    public void set(Object newState) {
        boolean set;
        do {
            Object oldState = stateRef.get();
            set = stateRef.compareAndSet(oldState, newState);
        } while (!set);
    }

    public Object swap(Function f) {
        Object oldState = stateRef.get();
        set(f.apply(oldState));
        return oldState;
    }

    public Object getState() {
        return stateRef.get();
    }
}
