package com.rhg135.derpyirc.core;

import com.google.common.base.Function;
import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IState;

import org.slf4j.Logger;

import java.util.concurrent.SynchronousQueue;

/**
 * Created by rhg135 on 02/03/15.
 */
public class Useful {
    public static Object object(IState<IMap> global, Object... path) throws IllegalArgumentException {
        //noinspection unchecked
        Object val = global.deref();
        for (Object o : path) {
            try {
                //noinspection unchecked
                val = ((IMap) val).get(o);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid path", e);
            }
        }
        return val;
    }

    public static boolean say(IState<IMap> global, Object toSay) {
        final IMap<String, SynchronousQueue> queues = subMap(global, "queues").deref();
        final SynchronousQueue display = queues.get("display");
        if (display != null) {
            try {
                display.put(toSay);
            } catch (InterruptedException e) {
                ((Logger) object(global, "logger")).error("Who interrupted me?", e);
            }
            return true;
        }
        return false;
    }

    public static IMap put(final IState<IMap> map, final Object key, final Object val) {
        map.swap(new Function<IMap, IMap>() {
            @Override
            public IMap apply(IMap input) {
                return input.assoc(key, val);
            }
        });
        return map.deref();
    }

    public static IState<IMap> subMap(final IState<IMap> globalState, final Object key) {
        return new IState<IMap>() {
            @Override
            public void swap(final Function<IMap, IMap> f) {
                globalState.swap(new Function<IMap, IMap>() {
                    @Override
                    public IMap apply(IMap input) {
                        return input.assoc(key, f.apply((IMap) input.get(key)));
                    }
                });
            }

            @Override
            public IMap deref() {
                return (IMap) globalState.deref().get(key);
            }
        };
    }

    public static void log(IMap global, String s) {

    }
}