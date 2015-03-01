package com.rhg135.derpyirc.android;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.PersistentVector;
import com.google.common.base.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rhg135 on 28/02/15.
 */
public class Utils {
    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static Function<PersistentVector, PersistentVector> conjer(final Object v) {
        return new Function<PersistentVector, PersistentVector>() {
            @Override
            public PersistentVector apply(PersistentVector input) {
                return input.plus(v);
            }
        };
    }

    public static Object get(Map m, Object key, Object value) {
        final Object inMap = m.get(key);
        if (inMap == null) {
            return value;
        } else {
            return inMap;
        }
    }
    public static PersistentMap<String, Object> read() {
        return null;
    }
}
