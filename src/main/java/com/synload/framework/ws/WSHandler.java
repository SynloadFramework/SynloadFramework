package com.synload.framework.ws;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.CloseEvent;
import com.synload.eventsystem.events.ConnectEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.JavascriptIncludes;
import com.synload.framework.handlers.Request;
import com.synload.framework.users.User;

@WebSocket
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class WSHandler{
	@JsonIgnore public Session session = null;
	public User user = null;
	@JsonIgnore public ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	/*@OnWebSocketFrame
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}*/
	
	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		SynloadFramework.users.remove(session);
		EventPublisher.raiseEventThread(new CloseEvent(this));
        //System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {
		this.session = session;
		SynloadFramework.users.add(session);
		try {
			session.getRemote().sendString(ow.writeValueAsString(new JavascriptIncludes()));
			System.out.println("[WS] "+session.getUpgradeRequest().getHeaders("X-Real-IP")+" connected!");
		} catch (IOException e) {
			e.printStackTrace();
		}
        //System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        EventPublisher.raiseEventThread(new ConnectEvent(this));
	}
	
	@OnWebSocketError
	public void onWebSocketError(Throwable t) {
		//System.out.println("Error: " + t.getMessage());
	}
	public void send(String data) throws IOException{
		//System.out.println(data);
		this.session.getRemote().sendString(data);
	}
	public class HandleRequest implements Runnable{
		WSHandler handler;
		Request request;
		public HandleRequest(WSHandler handler, Request request){
			this.request = request;
			this.handler = handler;
		}
		public void run (){
			try {
				WSRouting.page(this.handler,this.request);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@OnWebSocketMessage
	public void onWebSocketText(String message){
		ObjectMapper mapper = new ObjectMapper();
		try {
			Request request = mapper.readValue(message, Request.class);
			(new Thread(new HandleRequest(this,request))).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
        //System.out.println("[DEBUG]["+session.getUpgradeRequest().getHeaders("X-Real-IP")+"] " + message);
	}
}