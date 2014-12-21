package com.synload.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.CloseEvent;
import com.synload.eventsystem.events.ConnectEvent;
import com.synload.framework.OOnPage;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.JavascriptIncludes;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.users.User;

@WebSocket
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class WSHandler{
	@JsonIgnore public Session session = null;
	@JsonIgnore public List<String> queue = new ArrayList<String>();
	public User user = null;
	@JsonIgnore public boolean isSending = false;
	@JsonIgnore private Thread sendingThreadVar = null;
	/*@OnWebSocketFrame
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}*/
	
	@SuppressWarnings("deprecation")
	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		EventPublisher.raiseEvent(new CloseEvent(this),null);
		OOnPage.removeClient(this);
		SynloadFramework.users.remove(session);
		SynloadFramework.clients.remove(this);
		sendingThreadVar.stop();
		sendingThreadVar.interrupt();
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
			sendingThreadVar = (new Thread(new sendingThread(this)));
			sendingThreadVar.start();
			SynloadFramework.clients.add(this);
			if(SynloadFramework.isSiteDefaults()){
				session.getRemote().sendString(SynloadFramework.ow.writeValueAsString(new JavascriptIncludes()));
			}
			System.out.println("[WS] "+session.getUpgradeRequest().getHeaders("X-Real-IP")+" connected!");
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
        //System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        EventPublisher.raiseEvent(new ConnectEvent(this), null);
	}
	
	@OnWebSocketError
	public void onWebSocketError(Throwable t) {
		//System.out.println("Error: " + t.getMessage());
	}
	public void send(String data){
		queue.add(data);
	}
	public void send(Response r){
		OOnPage.newPage(this, r);
		try {
			queue.add(SynloadFramework.ow.writeValueAsString(r));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	public void send(Data r){
		try {
			queue.add(SynloadFramework.ow.writeValueAsString(r));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
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
				if(SynloadFramework.debug){
					e.printStackTrace();
				}
			}
		}
	}
	public class sendingThread implements Runnable{
		private WSHandler ws= null;
		public sendingThread(WSHandler ws){
			this.ws = ws;
		}
		@Override
		public void run() {
			while(true){
				try{
					if(ws.queue.size()>0){
						List<String> queueTemp = new ArrayList<String>(ws.queue);
						ws.queue = new ArrayList<String>();
						Iterator<String> queueIterator = queueTemp.iterator();
						while(queueIterator.hasNext()){
							ws.isSending=true;
							ws.session.getRemote().sendString(queueIterator.next(),new verifySend(ws));
						}
					}
					Thread.sleep(1);
				}catch(Exception e){
					if(SynloadFramework.debug){
						e.printStackTrace();
					}
				}
			}
		}
	}
	public class verifySend implements WriteCallback{
		private WSHandler ws= null;
		public verifySend(WSHandler ws){
			this.ws = ws;
		}
		@Override
		public void writeFailed(Throwable arg0) {
			this.ws.isSending=false;
		}
		@Override
		public void writeSuccess() {
			this.ws.isSending=false;
		}
	}
	@OnWebSocketMessage
	public void onWebSocketText(String message){
		ObjectMapper mapper = new ObjectMapper();
		try {
			Request request = mapper.readValue(message, Request.class);
			(new Thread(new HandleRequest(this,request))).start();
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
        //System.out.println("[DEBUG]["+session.getUpgradeRequest().getHeaders("X-Real-IP")+"] " + message);
	}
}