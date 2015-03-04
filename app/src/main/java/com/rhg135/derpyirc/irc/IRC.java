package com.rhg135.derpyirc.irc;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_ds.Persistents;
import com.rhg135.derpyirc.core.IMacro;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static com.rhg135.derpyirc.core.Useful.plusIfNone;
import static com.rhg135.derpyirc.core.Useful.run;

/**
 * Created by rhg135 on 03/03/15.
 */
public class IRC {
    public static final Logger logger = LoggerFactory.getLogger(IRC.class);
    public IRC(Map<String, PersistentMap> globalState) {
        final PersistentMap<String, String> config = globalState.get("config");
        final PersistentMap<String, String> newConfig =
                plusIfNone(config, "irc.version", "Derpy IRC 0.0.1");
        globalState.put("config", newConfig);
        globalState.put("bots", Persistents.arrayMap());
        globalState.put("irc", Persistents.arrayMap());

        final PersistentMap macros = globalState.get("macros");
        globalState.put("macros", macros.plus("aa51", new Connect()).plus("join", new Join()).plus("part", new Part())
                .plus("nick", new Nick()));
    }

    public static final class Join implements IMacro<Map<String, PersistentMap>> {
        @Override
        public void macro(final Map<String, PersistentMap> globalState, final String[] commandParts) {
            run(globalState, new Runnable() {
                @Override
                public void run() {
                    final String currentNetwork = (String) globalState.get("irc").get("current-network");
                    final PircBotX bot = (PircBotX) globalState.get("bots").get(currentNetwork);
                    if (bot != null) {
                        bot.sendIRC().joinChannel(commandParts[1]);
                    }
                }
            });
        }
    }

    public static final class Part implements IMacro<Map<String, PersistentMap>> {
        @Override
        public void macro(final Map<String, PersistentMap> globalState, final String[] commandParts) {
            run(globalState, new Runnable() {
                @Override
                public void run() {
                    // TODO: don't be so stateful
                    final String currentNetwork = (String) globalState.get("irc").get("current-network");
                    final PircBotX bot = (PircBotX) globalState.get("bots").get(currentNetwork);
                    if (bot != null) {
                        bot.sendRaw().rawLine("PART " + commandParts[1] + "\r\n");
                    }
                }
            });
        }
    }
    public static final class Connect implements IMacro<Map<String, PersistentMap>> {
        @Override
        public void macro(final Map<String, PersistentMap> globalState, String[] commandParts) {
            // FIXME: don't hardcode details
            final Configuration config = new Configuration.Builder()
                    .setRealName("DerpyIRC")
                    .setServer("chicago.androidarea51.com", 6667)
                    .setName("derpyirc13" + new Random().nextInt(10))
                    .addListener(new Adapters(globalState))
                    .buildConfiguration();
            final PircBotX bot = new PircBotX(config);
            final PersistentMap<String, PircBotX> bots = globalState.get("bots");
            globalState.put("bots", bots.plus("aa51", bot));
            run(globalState, new Runnable() {
                @Override
                public void run() {
                    try {
                        bot.startBot();
                    } catch (IOException e) {
                        logger.error("IO failed", e);
                        bots.minus("aa51");
                        globalState.put("bots", bots);
                    } catch (IrcException e) {
                        logger.error("IRC failed", e);
                        bots.minus("aa51");
                        globalState.put("bots", bots);
                    }
                }
            });
            globalState.put("irc", globalState.get("irc").plus("current-network", "aa51"));
        }
    }
}
