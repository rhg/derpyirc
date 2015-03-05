package com.rhg135.derpyirc.core.structures;

import com.google.common.base.Function;

/**
 * Created by rhg135 on 05/03/15.
 */
public interface IState<S> {
    public void swap(Function<S, S> f);

    public S deref();
}
