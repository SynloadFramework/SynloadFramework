package com.synload.eventsystem.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;
import com.synload.framework.ws.WSHandler;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;

/**
 * Created by Nathaniel on 5/20/2016.
 */
public class STMessageReceivedEvent extends EventClass {
    public Client serverTalkClient;
    public ConnectionDocument data;
    public STMessageReceivedEvent(Client client, ConnectionDocument data) {
        this.setHandler(Handler.EVENT);
        this.setType(Type.OTHER);
        this.setData(data);
        this.setServerTalkClient(client);
    }

    public ConnectionDocument getData() {
        return data;
    }

    public void setData(ConnectionDocument data) {
        this.data = data;
    }

    public Client getServerTalkClient() {
        return serverTalkClient;
    }

    public void setServerTalkClient(Client serverTalkClient) {
        this.serverTalkClient = serverTalkClient;
    }
}
