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
import java.util.concurrent.ExecutorService;

import static com.rhg135.derpyirc.core.Useful.plusIfNone;

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

        globalState.put("macros", globalState.get("macros").plus("aa51", new Connect()));
    }

    public static final class Connect implements IMacro<Map<String, PersistentMap>> {
        @Override
        public void macro(final Map<String, PersistentMap> globalState, String[] commandParts) {
            // FIXME: don't hardcode details
            final ExecutorService pool = (ExecutorService) globalState.get("misc").get("pool");
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    final Configuration config = new Configuration.Builder()
                            .setRealName("DerpyIRC")
                            .setServer("chicago.androidarea51.com", 6667)
                            .setName("derpyirc13" + new Random().nextInt(10))
                            .addListener(new Adapters(globalState))
                            .buildConfiguration();
                    final PircBotX bot = new PircBotX(config);
                    final PersistentMap<Integer, PircBotX> bots = globalState.get("bots");
                    globalState.put("bots", bots.plus(bot.hashCode(), bot));
                    try {
                        bot.startBot();
                    } catch (IOException e) {
                        logger.error("IO failed", e);
                    } catch (IrcException e) {
                        logger.error("IRC failed", e);
                    }
                }
            });

        }
    }
}
