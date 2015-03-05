package com.rhg135.derpyirc.core.structures;

/**
 * Created by rhg135 on 05/03/15.
 */
public interface IMapProvider<K, V> {
    public IMap<K, V> newIMap();
}
