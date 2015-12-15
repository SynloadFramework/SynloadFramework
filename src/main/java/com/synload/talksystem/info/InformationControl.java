package com.synload.talksystem.info;

import com.synload.eventsystem.EventPublisher;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;

public class InformationControl {
    public void infoReceived(Client c, ConnectionDocument doc){
        if(doc.getClass() == InformationDocument.class){
            ServerTalkInformationEvent ev = new ServerTalkInformationEvent(c,  doc);
            EventPublisher.raiseEvent(ev, true, "");
        }
    }
}
