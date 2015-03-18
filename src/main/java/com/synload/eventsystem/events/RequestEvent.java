package com.synload.eventsystem.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.framework.handlers.Request;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.framework.ws.WSHandler;

public class RequestEvent extends EventClass {
    public WSHandler session = null;
    public Request request = null;

    public RequestEvent(WSHandler session, Request request) {
        this.setRequest(request);
        this.setHandler(Handler.EVENT);
        this.setType(Type.WEBSOCKET);
        this.setSession(session);
        this.setTrigger(new String[] { request.getRequest(), request.getPage() });
    }

    public Request getRequest() {
        return request;
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
