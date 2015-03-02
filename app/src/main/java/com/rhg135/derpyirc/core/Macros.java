package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class Macros {

    public static final Logger logger = LoggerFactory.getLogger(Macros.class);

    public static PersistentMap<String, Object> loadDebugMacros(PersistentMap<String, Object> globalState) {
        final PersistentMap<String, IMacro<PersistentMap<String, Object>>> macrosMap =
                ((PersistentMap<String, IMacro<PersistentMap<String, Object>>>) globalState.get("macros"))
                .plus("state", new StateMacro<PersistentMap<String, Object>>());
        return globalState.plus("macros", macrosMap);
    }
    public static void stateMacro(Object globalState,
                                  String[] commandParts) {
        logger.debug(globalState.toString());
    }
}
