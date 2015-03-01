package com.rhg135.derpyirc.android;

import com.github.krukow.clj_ds.PersistentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rhg135 on 28/02/15.
 */
public class Utils {
    public static final Logger logger = LoggerFactory.getLogger(Utils.class);

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
