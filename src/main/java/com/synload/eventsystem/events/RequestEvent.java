package com.synload.eventsystem.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class RequestEvent extends EventClass {
    public WSHandler session = null;
    public Request request = null;

    public RequestEvent(WSHandler session, Request request) {
        this.setRequest(request);
        this.setHandler(Handler.WSEVENT);
        this.setType(Type.WEBSOCKET);
        this.setSession(session);
        this.setResponse(session); // set response handler
        //this.setTrigger(new String[] { request.getMethod(), request.getAction() });
    }

    public Request getRequest() {
        return request;
    }

    public void respond(Response r){
        r.setReference(this.getRequest().getReference());
        this.getSession().send(r);
    }
    public void respond(Data r){
        r.setReference(this.getRequest().getReference());
        this.getSession().send(r);
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public WSHandler getSession() {
        return session;
    }

    public void setSession(WSHandler session) {
        this.session = session;
    }
}
