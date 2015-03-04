package com.rhg135.derpyirc.irc;

import com.github.krukow.clj_ds.PersistentMap;
import com.rhg135.derpyirc.core.Useful;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rhg135 on 03/03/15.
 */
public class Adapters extends ListenerAdapter {
    final Map<String, PersistentMap> state;
    public static final Logger logger = LoggerFactory.getLogger(Adapters.class);

    public Adapters(Map<String, PersistentMap> globalState) {
        state = globalState;
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        // TODO: add network info
        Useful.say(state, "[/" + event.getUser().getNick() + "] - [" + event.getMessage() + "]");
    }
}
