package com.rhg135.derpyirc.irc;

import com.rhg135.derpyirc.core.IMacro;
import com.rhg135.derpyirc.core.Useful;
import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IMapProvider;
import com.rhg135.derpyirc.core.structures.IState;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * Created by rhg135 on 03/03/15.
 */
public class IRC {
    public static final Logger logger = LoggerFactory.getLogger(IRC.class);

    public static IMap loadPlugin(IMap global, ExecutorService pool, IMapProvider mapProvider) {
        final IMap newMacros = ((IMap) global.get("macros"))
                .assoc("aa51", new Connect())
                .assoc("join", new Join())
                .assoc("say", new Say())
                .assoc("part", new Part());
        return global
                .assoc("pool", pool)
                .assoc("irc", mapProvider.newIMap())
                .assoc("bots", mapProvider.newIMap())
                .assoc("macros", newMacros);
    }

    public static final class Say implements IMacro<IState<IMap>> {
        @Override
        public void macro(final IState<IMap> globalState, String[] commandParts) {
            // TODO: this could fail
            final String currentNetwork = (String) Useful.object(globalState, "irc", "current-network");
            final PircBotX bot = (PircBotX) Useful.object(globalState, "bots", currentNetwork);
            final String target = (String) Useful.object(globalState, "irc", "current-target");
            final String msg = commandParts[1];
            Useful.run(globalState, new Runnable() {
                @Override
                public void run() {
                    ((PircBotX) bot).sendIRC().message(target, msg);
                }
            });
        }
    }

    public static final class Join implements IMacro<IState<IMap>> {
        @Override
        public void macro(final IState<IMap> globalState, final String[] commandParts) {
            Useful.run(globalState, new Runnable() {
                @Override
                public void run() {
                    final String currentNetwork = (String) Useful.object(globalState, "irc", "current-network");
                    final PircBotX bot = (PircBotX) Useful.object(globalState, "bots", currentNetwork);
                    if (bot != null) {
                        bot.sendIRC().joinChannel(commandParts[1]);
                        Useful.put(Useful.subMap(globalState, "irc"), "current-target", commandParts[1]);
                    }
                }
            });
        }
    }

    public static final class Part implements IMacro<IState<IMap>> {
        @Override
        public void macro(final IState<IMap> globalState, final String[] commandParts) {
            Useful.run(globalState, new Runnable() {
                @Override
                public void run() {
                    // TODO: don't be so stateful
                    final String currentNetwork = (String) Useful.object(globalState, "irc", "current-network");
                    final PircBotX bot = (PircBotX) Useful.object(globalState, "bots", currentNetwork);
                    if (bot != null) {
                        bot.sendRaw().rawLine("PART " + commandParts[1] + "\r\n");
                    }
                }
            });
        }
    }

    public static final class Connect implements IMacro<IState<IMap>> {
        @Override
        public void macro(final IState<IMap> globalState, String[] commandParts) {
            // FIXME: don't hardcode details
            final Configuration config = new Configuration.Builder()
                    .setRealName("DerpyIRC")
                    .setServer("chicago.androidarea51.com", 6667)
                    .setName("derpyirc13" + new Random().nextInt(10))
                    .addListener(new Adapters(globalState))
                    .buildConfiguration();
            final PircBotX bot = new PircBotX(config);
            final IState<IMap> bots = Useful.subMap(globalState, "bots");
            Useful.put(bots, "aa51", bot);
            ((ExecutorService) Useful.object(globalState, "pool")).submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        bot.startBot();
                    } catch (IOException e) {
                        logger.error("IO failed", e);
                        Useful.remove(bots, "aa51");
                    } catch (IrcException e) {
                        logger.error("IRC failed", e);
                        Useful.remove(bots, "aa51");
                    }
                }
            });
            Useful.put(Useful.subMap(globalState, "irc"), "current-network", "aa51");
        }
    }
}
