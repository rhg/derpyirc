package com.rhg135.derpyirc.core;

import com.google.common.base.Function;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rhg135 on 01/03/15.
 */
public class AtomicState<S> implements IAtomic<S> {
    protected final AtomicReference<S> stateRef = new AtomicReference<S>(null);

    @Override
    public S reset(S newState) {
        boolean set;
        do {
            S oldState = stateRef.get();
            set = stateRef.compareAndSet(oldState, newState);
        } while (!set);
        return newState;
    }

    @Override
    public S swap(Function<S, S> f) {
        final S oldState = stateRef.get();
        final S newState = f.apply(oldState);
        return reset(newState);
    }

    @Override
    public S getState() {
        return stateRef.get();
    }
}
