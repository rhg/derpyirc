package com.rhg135.derpyirc.core;

import com.google.common.base.Function;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rhg135 on 01/03/15.
 */
public class AtomicState<E> {
    protected final AtomicReference<E> stateRef = new AtomicReference<E>(null);

    public void set(E newState) {
        boolean set;
        do {
            E oldState = stateRef.get();
            set = stateRef.compareAndSet(oldState, newState);
        } while (!set);
    }

    public E swap(Function<E, E> f) {
        final E oldState = stateRef.get();
        final E newState = f.apply(oldState);
        set(newState);
        return newState;
    }

    public E getState() {
        return stateRef.get();
    }
}
