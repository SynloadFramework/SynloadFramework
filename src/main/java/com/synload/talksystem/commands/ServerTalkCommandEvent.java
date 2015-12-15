package com.synload.talksystem.commands;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;

public class ServerTalkCommandEvent extends EventClass {
    public CommandDocument commandDocument;
    public Client client;
    public ServerTalkCommandEvent(Client client, ConnectionDocument commandDocument){
        this.commandDocument = (CommandDocument) commandDocument;
        this.client = client;
        this.setHandler(Handler.EVENT);
        this.setType(Type.OTHER);
    }
    public CommandDocument getCommandDocument() {
        return commandDocument;
    }
    public void setCommandDocument(CommandDocument commandDocument) {
        this.commandDocument = commandDocument;
    }
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
}
