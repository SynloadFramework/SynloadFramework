package com.synload.talksystem.filetransfer;

import java.util.UUID;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.framework.ws.WSHandler;
import com.synload.talksystem.Client;

public class FileReceiveEvent extends EventClass  {
    public String fileName;
    public String tempName;
    public UUID chain;
    public String tempFilePath;
    public Client client;
    public FileReceiveEvent(Client c, String fileName, String tempName, String tempFilePath, UUID chain) {
        this.setHandler(Handler.EVENT);
        this.setChain(chain);
        this.setTempName(tempName);
        this.setType(Type.WEBSOCKET);
        this.setTempFilePath(tempFilePath);
        this.setFileName(fileName);
        this.setClient(c);
    }

    
    public String getTempFilePath() {
        return tempFilePath;
    }


    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
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

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public UUID getChain() {
        return chain;
    }

    public void setChain(UUID chain) {
        this.chain = chain;
    }
}
