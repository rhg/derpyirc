package com.rhg135.derpyirc.core;

import android.support.annotation.NonNull;

import com.github.krukow.clj_ds.PersistentMap;
import com.google.common.base.Function;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by rhg135 on 03/03/15.
 */
public class HistoricMap<K, V> implements Map<K, V>, IAtomic<PersistentMap<K, V>> {
    protected final AtomicState<PersistentMap<K, V>> state = new AtomicState<>();

    @Override
    public PersistentMap<K, V> swap(Function<PersistentMap<K, V>, PersistentMap<K, V>> function) {
        return state.swap(function);
    }

    @Override
    public PersistentMap<K, V> reset(PersistentMap<K, V> newState) {
        // TODO: history
        return state.reset(newState);
    }

    @Override
    public PersistentMap<K, V> getState() {
        return state.getState();
    }

    @Override
    public void clear() {
        state.swap(new Function<PersistentMap<K, V>, PersistentMap<K, V>>() {
            @Override
            public PersistentMap<K, V> apply(PersistentMap<K, V> input) {
                return input.zero();
            }
        });
    }

    @Override
    public boolean containsKey(Object key) {
        return getState().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getState().containsValue(value);
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return getState().entrySet();
    }

    @Override
    public V get(Object key) {
        return getState().get(key);
    }

    @Override
    public boolean isEmpty() {
        return getState().isEmpty();
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return getState().keySet();
    }

    @Override
    public V put(final K key, final V value) {
        // TODO: keep history
        swap(new Function<PersistentMap<K, V>, PersistentMap<K, V>>() {
            @Override
            public PersistentMap<K, V> apply(PersistentMap<K, V> input) {
                return input.plus(key, value);
            }
        });
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        // TODO: implement
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public V remove(final Object key) {
        final V val = get(key);
        swap(new Function<PersistentMap<K, V>, PersistentMap<K, V>>() {
            @Override
            public PersistentMap<K, V> apply(PersistentMap<K, V> input) {
                return input.minus((K) key);
            }
        });
        return val;
    }

    @Override
    public int size() {
        return getState().size();
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return getState().values();
    }

    @Override
    public String toString() {
        return getState().toString();
    }
}
