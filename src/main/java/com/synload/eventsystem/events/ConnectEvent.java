/**
 * @author Nathaniel
 *
 */
package com.synload.eventsystem.events;

import com.synload.eventsystem.Event;
import com.synload.framework.ws.WSHandler;

public class ConnectEvent extends Event{
    public WSHandler session;
    public ConnectEvent(WSHandler session){
    	this.setSession(session);
    }
	public WSHandler getSession() {
		return session;
	}
	public void setSession(WSHandler session) {
		this.session = session;
	}
}