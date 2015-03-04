package com.rhg135.derpyirc.irc;

import com.github.krukow.clj_ds.PersistentMap;
import com.google.common.base.Function;
import com.rhg135.derpyirc.core.IMacro;

import org.pircbotx.PircBotX;

import java.util.Map;

import static com.rhg135.derpyirc.core.Useful.say;
import static com.rhg135.derpyirc.irc.IRCUtils.botForNetwork;
import static com.rhg135.derpyirc.irc.IRCUtils.doToBot;

/**
 * Created by rhg135 on 04/03/15.
 */
public class Nick implements IMacro<Map<String, PersistentMap>> {
    @Override
    public void macro(Map<String, PersistentMap> globalState, final String[] commandParts) {
        if (commandParts.length == 3) {
            doToBot(botForNetwork(globalState, commandParts[1]), new Function<PircBotX, Void>() {
                @Override
                public Void apply(PircBotX input) {
                    input.sendIRC().changeNick(commandParts[2]);
                    return null;
                }
            });
        } else {
            say(globalState, "Invalid arguments for " + commandParts[0]);
        }
    }
}
