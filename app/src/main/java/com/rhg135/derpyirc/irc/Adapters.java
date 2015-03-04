package com.rhg135.derpyirc.irc;

import com.github.krukow.clj_ds.PersistentMap;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;

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
        final SynchronousQueue<String> display = (SynchronousQueue<String>) state.get("io").get("display");
        try {
            display.put(event.getMessage());
        } catch (InterruptedException e) {
            logger.error("Who interrupted me?", e);
        }
    }
}
