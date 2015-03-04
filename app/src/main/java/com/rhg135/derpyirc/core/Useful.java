package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;
import com.google.common.base.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by rhg135 on 02/03/15.
 */
public class Useful {
    public static final Logger logger = LoggerFactory.getLogger(Useful.class);
    public static void run(Map<String, PersistentMap> globalState, Runnable r) {
        final ExecutorService pool = (ExecutorService) globalState.get("misc").get("pool");
        pool.submit(r);
    }

    public static void say(Map<String, PersistentMap> globalState, String line) {
        final SynchronousQueue<String> outputQueue = (SynchronousQueue<String>) globalState.get("io").get("display");
        try {
            outputQueue.put(line);
        } catch (InterruptedException e) {
            logger.error("Who interrupted me?", e);
        }
    }
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

    public static PersistentMap plusIfNone(PersistentMap m, Object k, Object v) {
        final Object o = m.get(k);
        if (o == null) {
            return m.plus(k, v);
        } else {
            return m;
        }
    }
}
