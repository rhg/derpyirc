package com.rhg135.derpyirc.core;

import com.google.common.base.Function;

/**
 * Created by rhg135 on 03/03/15.
 */
public interface IAtomic<S> {
    public S swap(Function<S, S> function);

    public S reset(S newState);

    public S getState();
}
