package com.synload.eventsystem.events;

import com.synload.eventsystem.Event;
import com.synload.framework.ws.WSHandler;

public class CloseEvent extends Event {
	public WSHandler session;
	public CloseEvent(WSHandler session){
    	this.setSession(session);
    }
	public WSHandler getSession() {
		return session;
	}
	public void setSession(WSHandler session) {
		this.session = session;
	}
}
