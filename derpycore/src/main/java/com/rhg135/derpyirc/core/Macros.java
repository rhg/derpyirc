package com.rhg135.derpyirc.core;

import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IMapProvider;
import com.rhg135.derpyirc.core.structures.IState;

import org.slf4j.Logger;

import java.util.Arrays;

/**
 * Created by Ricardo on 3/2/2015.
 */
public class Macros {

    public static IMap<String, IMacro> loadDebugMacros(IMapProvider<String, IMacro> mapProvider) {
        return mapProvider.newIMap()
                .assoc("state", new StateMacro())
                .assoc("set", new SetCommand());
    }

    public final static class SetCommand implements IMacro<IState<IMap>> {
        @Override
        public void macro(IState<IMap> globalState, final String[] commandParts) {
            if (commandParts.length == 3) {
                Useful.put(Useful.subMap(globalState, "config"), commandParts[1], commandParts[2]);
            } else {
                Useful.say(Useful.subMap(globalState, "queues"), "Invalid arguments to SET " + Arrays.toString(commandParts));
            }
        }
    }

    public final static class StateMacro implements IMacro<IState<IMap>> {
        @Override
        public void macro(IState<IMap> globalState, String[] commandParts) {
            ((Logger) Useful.object(globalState, "logger")).debug("State: " + globalState.deref().toString());
            Useful.say(globalState, globalState.deref().toString());
        }
    }

}
