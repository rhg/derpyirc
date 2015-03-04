package com.rhg135.derpyirc.irc;

import com.github.krukow.clj_ds.PersistentMap;
import com.google.common.base.Function;

import org.pircbotx.PircBotX;

import java.util.Map;

/**
 * Created by rhg135 on 04/03/15.
 */
public class IRCUtils {
    public static PircBotX botForNetwork(Map<String, PersistentMap> globalState, String server) {
        return (PircBotX) globalState.get("bots").get(server);
    }

    public static void doToBot(PircBotX botX, Function<PircBotX, Void> function) {
        if (botX != null) {
            function.apply(botX);
        }
    }
}
