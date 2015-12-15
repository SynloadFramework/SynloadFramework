package com.synload.talksystem.commands;

import com.synload.eventsystem.EventPublisher;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;

public class CommandControl {
    public void command(Client c, ConnectionDocument doc){
        if(doc.getClass() == CommandDocument.class){
            ServerTalkCommandEvent ev = new ServerTalkCommandEvent(c, doc);
            EventPublisher.raiseEvent(ev, true, "");
        }
    }
}
