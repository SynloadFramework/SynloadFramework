package com.synload.eventsystem.events;

import com.synload.eventsystem.Event;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;

public class RequestEvent extends Event {
	public WSHandler session=null;
	public Request request=null;
	public RequestEvent(WSHandler session, Request request){
		this.setRequest(request);
		this.setSession(session);
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
