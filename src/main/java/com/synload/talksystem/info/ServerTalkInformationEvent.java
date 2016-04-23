package com.synload.talksystem.info;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;

public class ServerTalkInformationEvent extends EventClass {
    private Client client;
    private InformationDocument iD;
    public ServerTalkInformationEvent( Client c, ConnectionDocument iD) {
        this.setHandler(Handler.EVENT);
        this.setType(Type.OTHER);
        this.setiD((InformationDocument) iD);
        this.setClient(c);
    }
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public InformationDocument getiD() {
        return iD;
    }
    public void setiD(InformationDocument iD) {
        this.iD = iD;
    }
}
