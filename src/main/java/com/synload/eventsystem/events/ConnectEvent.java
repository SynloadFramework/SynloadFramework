/**
 * @author Nathaniel
 *
 */
package com.synload.eventsystem.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.framework.ws.WSHandler;

public class ConnectEvent extends EventClass{
    public WSHandler session;
    public ConnectEvent(WSHandler session){
    	this.setSession(session);
    	this.setHandler(Handler.EVENT);
    }
	public WSHandler getSession() {
		return session;
	}
	public void setSession(WSHandler session) {
		this.session = session;
	}
}