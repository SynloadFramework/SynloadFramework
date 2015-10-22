package com.synload.talksystem;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class ConnectionDocument implements Serializable {
    public String typeName = "";
    public UUID chain = null;
    public ConnectionDocument(String type, UUID chain){
        this.chain = chain;
        this.typeName = type;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public UUID getChain() {
        return chain;
    }
    public void setChain(UUID chain) {
        this.chain = chain;
    }
}
