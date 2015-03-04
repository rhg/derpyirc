package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;

/**
 * Created by rhg135 on 03/03/15.
 */
public interface IHistoricMap<K, V> {
    public PersistentMap<K, V> lastState();

    public IHistoricMap<K, V> lastHistory();
}
