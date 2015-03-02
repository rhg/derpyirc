package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;
import com.google.common.base.Function;

/**
 * Created by rhg135 on 02/03/15.
 */
public class Useful {
    public static Function<PersistentMap<String, Object>, PersistentMap<String, Object>> update(final String key,
                                                                                                final Function<Object, Object> f) {
        return new Function<PersistentMap<String, Object>, PersistentMap<String, Object>>() {
            @Override
            public PersistentMap<String, Object> apply(PersistentMap<String, Object> input) {
                final Object newObj = f.apply(input.get(key));
                return input.plus(key, newObj);
            }
        };
    }
}
