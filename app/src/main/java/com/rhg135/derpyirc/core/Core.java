package com.rhg135.derpyirc.core;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.PersistentVector;
import com.github.krukow.clj_ds.Persistents;
import com.google.common.base.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by rhg135 on 01/03/15.
 */
public final class Core {

    private static final Logger logger = LoggerFactory.getLogger(Core.class);

    public static void dispatch(AtomicState<PersistentMap<String, Object>> globalState, final String commandLine) {
        logger.info("Dispatching on " + commandLine);

        if (commandLine.startsWith("?")) {
            final String[] macroParts = commandLine.split("\\s+", 1);
            final String macroName = macroParts[0].substring(1);
            logger.debug("Dispatching on macro " + macroName);
            PersistentMap<String, IMacro<PersistentMap>> loadedMacros = (PersistentMap<String, IMacro<PersistentMap>>) globalState.getState().get("macros");
            if (loadedMacros == null) {
                logger.info("No Loaded Macros");
            } else {
                try {
                    final IMacro<PersistentMap> macro = loadedMacros.get(macroName);
                    if (macro == null) {
                        // TODO: don't use exceptions for flow control
                        throw new RuntimeException("No such macro");
                    }
                    macro.macro(globalState.getState(), macroParts);
                } catch (Throwable e) {
                    logger.error("Macro failure", e);
                }
            }
        }
    }
}
