package com.synload.talksystem.info;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.synload.talksystem.ConnectionDocument;

@SuppressWarnings("serial")
public class InformationDocument extends ConnectionDocument {
    private String type;
    private Map<String, Object> objects = new HashMap<String, Object>();
    public InformationDocument(String type, UUID chain) {
        super("syn-info", chain);
        this.type = type;
    }
    public Map<String, Object> getObjects() {
        return objects;
    }
    public void setObjects(Map<String, Object> objects) {
        this.objects = objects;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
