package com.synload.talksystem.filetransfer;

import java.util.UUID;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.framework.ws.WSHandler;
import com.synload.talksystem.Client;

public class FileRequestEvent  extends EventClass {
    public WSHandler session;
    public String fileName;
    public UUID id;
    public Client client;
    public FileRequestEvent(WSHandler session, Client c, String fileName, UUID id) {
        this.setSession(session);
        this.setHandler(Handler.EVENT);
        this.setType(Type.WEBSOCKET);
        this.setFileName(fileName);
        this.setId(id);
        this.setClient(c);
    }

    public WSHandler getSession() {
        return session;
    }
    public void setSession(WSHandler session) {
        this.session = session;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
