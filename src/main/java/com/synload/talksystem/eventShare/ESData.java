package com.synload.talksystem.eventShare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathaniel on 7/26/2016.
 */
public class ESData {
    public String identifier;
    public String data;
    public int type;
    public List<String> headers = new ArrayList<>();


    public ESData(String identifier) {
        this.identifier = identifier;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public List<String> getHeaders() {
        return headers;
    }
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
