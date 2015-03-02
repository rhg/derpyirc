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

    public static void dispatch(AtomicState globalState, final String commandLine) {
        logger.info("Dispatching on " + commandLine);
        globalState.swap(new Function() {
            @Override
            public Object apply(Object input) {
                if (input == null) {
                    return null;
                } else {
                    PersistentVector history = (PersistentVector) ((PersistentMap) input).get("history.input");
                    if (history == null) {
                        history = Persistents.vector();
                    }
                    return history.plus(commandLine);
                }
            }
        });

        if (commandLine.startsWith("?")) {
            final String[] macroParts = commandLine.split("\\s+", 1);
            final String macroName = macroParts[0].substring(1);
            logger.debug("Dispatching on macro " + macroName);
            PersistentMap loadedMacros = (PersistentMap) ((PersistentMap) globalState.getState()).get("macros");
            if (loadedMacros == null) {
                logger.info("No such macro " + macroName);
                logger.debug("Loaded Macros: " + loadedMacros);
                return;
            } else {
                try {
                    final Class klass = (Class) loadedMacros.get(macroName);
                    final Method method = klass.getDeclaredMethod("macro", PersistentMap.class, macroParts.getClass());
                    method.invoke(null, globalState, macroParts);
                } catch (Throwable e) {
                    logger.error("Macro failure", e);
                }
            }
        }
    }
}
