package com.synload.eventsystem.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;
import com.synload.framework.ws.WSHandler;

public class CloseEvent extends EventClass {
    public WSHandler session;

    public CloseEvent(WSHandler session) {
        this.setSession(session);
        this.setHandler(Handler.EVENT);
        this.setType(Type.OTHER);
    }

    public WSHandler getSession() {
        return session;
    }

    public void setSession(WSHandler session) {
        this.session = session;
    }
}
