package com.rhg135.derpyirc.core;

import com.google.common.base.Function;
import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IMapProvider;
import com.rhg135.derpyirc.core.structures.IState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rhg135 on 01/03/15.
 */
public final class Core {
    @SuppressWarnings("unchecked")
    public static IState<IMap> newState(IMapProvider mapProvider, Queue displayQueue) {
        final Logger logger = LoggerFactory.getLogger(Core.class);
        final IMap queues = mapProvider.newIMap().assoc("display", displayQueue);
        final IMap newMap = mapProvider.newIMap()
                .assoc("logger", logger)
                .assoc("macros", Macros.loadDebugMacros(mapProvider))
                .assoc("config", mapProvider.newIMap())
                .assoc("queues", queues)
                .assoc("version", "0.0.1");
        return new IState<IMap>() {
            final AtomicReference<IMap> state = new AtomicReference<>(newMap);

            @Override
            public void swap(Function<IMap, IMap> f) {
                boolean set;
                do {
                    set = state.compareAndSet(deref(), f.apply(deref()));
                } while (!set);
                logger.debug("State has been swapped");
            }

            @Override
            public IMap deref() {
                return state.get();
            }
        };
    }


    public static void dispatch(IState<IMap> globalState, final String commandLine) {
        final Logger logger = (Logger) Useful.object(globalState, "logger");
        logger.info("Dispatching on " + commandLine);

        final String[] macroParts;
        if (commandLine.startsWith("?")) {
            macroParts = commandLine.split("\\s+", 1);
        } else {
            macroParts = commandLine.split("\\s+");
        }
        final String macroName = macroParts[0].substring(1);
        logger.debug("Dispatching on macro " + macroName);

        //noinspection unchecked
        IMap<String, IMacro> loadedMacros = (IMap<String, IMacro>) Useful.object(globalState, "macros");
        if (loadedMacros.isEmpty()) {
            logger.info("No Loaded Macros");
        } else {
            try {
                final IMacro macro = loadedMacros.get(macroName);
                if (macro == null) {
                    // TODO: don't use exceptions for flow control
                    throw new RuntimeException("No such macro");
                }
                //noinspection unchecked
                macro.macro(globalState, macroParts);
            } catch (Throwable e) {
                logger.error("Macro failure", e);
            }
        }
    }
}