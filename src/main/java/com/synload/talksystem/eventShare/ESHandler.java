package com.synload.talksystem.eventShare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.framework.OOnPage;
import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.Responder;
import com.synload.framework.ws.WSHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathaniel on 7/27/2016.
 */
public class ESHandler extends Responder{
    public ESData esdata;
    public EventShare es = null;
    public Map<String, Object> sessionData = new HashMap<String, Object>();
    public ESHandler(String identifier, EventShare es){
        esdata = new ESData(identifier);
        this.es = es;
    }

    @Override
    public void send(String data) {
        esdata.setData(data);
        es.respond(esdata);
    }

    public void send(Response r) {
        //OOnPage.newPage(this, r);
        try {
            esdata.setData(SynloadFramework.ow.writeValueAsString(r));
            es.respond(esdata);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public void send(Data r) {
        try {
            esdata.setData(SynloadFramework.ow.writeValueAsString(r));
            es.respond(esdata);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public ESData getEsdata() {
        return esdata;
    }

    public void setEsdata(ESData esdata) {
        this.esdata = esdata;
    }

    public Map<String, Object> getSessionData() {
        return sessionData;
    }

    public void setSessionData(Map<String, Object> sessionData) {
        this.sessionData = sessionData;
    }

    public EventShare getEs() {
        return es;
    }

    public void setEs(EventShare es) {
        this.es = es;
    }
}
