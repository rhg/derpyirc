package com.rhg135.derpyirc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rhg135 on 01/03/15.
 */
public final class Core {

    private static final Logger logger = LoggerFactory.getLogger(Core.class);

    public static void dispatch(Map globalState, final String commandLine) {
        logger.info("Dispatching on " + commandLine);

        if (commandLine.startsWith("?")) {
            final String[] macroParts = commandLine.split("\\s+", 1);
            final String macroName = macroParts[0].substring(1);
            logger.debug("Dispatching on macro " + macroName);
            Map<String, IMacro<Map>> loadedMacros = (Map<String, IMacro<Map>>) globalState.get("macros");
            if (loadedMacros == null) {
                logger.info("No Loaded Macros");
            } else {
                try {
                    final IMacro<Map> macro = loadedMacros.get(macroName);
                    if (macro == null) {
                        // TODO: don't use exceptions for flow control
                        throw new RuntimeException("No such macro");
                    }
                    macro.macro(globalState, macroParts);
                } catch (Throwable e) {
                    logger.error("Macro failure", e);
                }
            }
        } else if (commandLine.startsWith("/")) {
            final String[] macroParts = commandLine.split("\\s+");
            final String macroName = macroParts[0].substring(1);
            logger.debug("Dispatching on command " + macroName);
            Map<String, IMacro<Map>> loadedMacros = (Map) globalState.get("macros");
            if (loadedMacros == null) {
                logger.info("No Loaded Macros");
            } else {
                try {
                    final IMacro<Map> macro = loadedMacros.get(macroName);
                    if (macro == null) {
                        // TODO: don't use exceptions for flow control
                        throw new RuntimeException("No such macro");
                    }
                    macro.macro(globalState, macroParts);
                } catch (Throwable e) {
                    logger.error("Macro failure", e);
                }
            }
        }
    }
}
