package com.rhg135.derpyirc.irc;

import com.rhg135.derpyirc.core.Useful;
import com.rhg135.derpyirc.core.structures.IMap;
import com.rhg135.derpyirc.core.structures.IState;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

/**
 * Created by rhg135 on 03/03/15.
 */
public class Adapters extends ListenerAdapter {
    final IState<IMap> state;

    public Adapters(IState<IMap> globalState) {
        state = globalState;
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        // TODO: add network info
        Useful.say(state, "[/" + event.getUser().getNick() + "] - [" + event.getMessage() + "]");
    }
}
