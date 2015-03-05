package com.rhg135.derpyirc.core.structures;

/**
 * Created by rhg135 on 05/03/15.
 */
public interface IMap<K, V> {
    public IMap<K, V> assoc(K key, V val);

    public IMap<K, V> dissoc(K key);

    public V get(K key);

    public boolean isEmpty();
}
