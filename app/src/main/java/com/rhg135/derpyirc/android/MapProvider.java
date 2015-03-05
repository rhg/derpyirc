package com.rhg135.derpyirc.android;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.Persistents;
import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IMapProvider;

/**
 * Created by rhg135 on 05/03/15.
 */
public class MapProvider implements IMapProvider {
    @Override
    public IMap newIMap() {
        return arrayMapToIMap(Persistents.arrayMap());
    }

    private IMap arrayMapToIMap(final PersistentMap persistentMap) {
        return new IMap() {
            @Override
            public IMap assoc(Object key, Object val) {
                if (persistentMap.size() == 6) {
                    return arrayMapToIMap(Persistents.hashMap(persistentMap).plus(key, val));
                } else {
                    return arrayMapToIMap(persistentMap.plus(key, val));
                }
            }

            @Override
            public IMap dissoc(Object key) {
                return arrayMapToIMap(persistentMap.minus(key));
            }

            @Override
            public Object get(Object key) {
                return persistentMap.get(key);
            }

            @Override
            public boolean isEmpty() {
                return persistentMap.isEmpty();
            }
        };
    }
}
