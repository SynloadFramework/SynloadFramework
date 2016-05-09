package com.synload.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import com.synload.framework.Log;
import com.synload.framework.OOnPage;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Connected;
import com.synload.framework.elements.EncryptAuth;
import com.synload.framework.elements.JavascriptIncludes;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.security.PKI;

@WebSocket
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class WSHandler {
    @JsonIgnore
    public Session session = null;
    @JsonIgnore
    public List<String> queue = new ArrayList<String>();
    public Map<String, Object> sessionData = new HashMap<String, Object>();
    private PKI pki;
    public boolean encrypt = false;
    public List<String> flags = new ArrayList<String>();
    @JsonIgnore
    public boolean isSending = false;
    @JsonIgnore
    private Thread sendingThreadVar = null;

    /*
     * @OnWebSocketFrame public void onWebSocketBinary(byte[] arg0, int arg1,
     * int arg2) { // TODO Auto-generated method stub
     * 
     * }
     */

    @SuppressWarnings("deprecation")
    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        EventPublisher.raiseEvent(new CloseEvent(this), null);
        OOnPage.removeClient(this);
        SynloadFramework.users.remove(session);
        SynloadFramework.clients.remove(this);
        sendingThreadVar.stop();
        sendingThreadVar.interrupt();
        Log.debug("Close: statusCode=" + statusCode + ", reason=" + reason,
                this.getClass());
    }

    public Map<String, Object> getSessionData() {
		return sessionData;
	}

	public void setSessionData(Map<String, Object> sessionData) {
		this.sessionData = sessionData;
	}

	public PKI getPki() {
		return pki;
	}

	public void setPki(PKI pki) {
		this.pki = pki;
	}

	@OnWebSocketConnect
    public void onWebSocketConnect(Session session) {

        this.session = session;
        SynloadFramework.users.add(session);
        sendingThreadVar = (new Thread(new sendingThread(this)));
        sendingThreadVar.start();
        SynloadFramework.clients.add(this);
        if (SynloadFramework.isEncryptEnabled()) {
        	send(new JavascriptIncludes());
        	try {
				this.setPki(new PKI());
				this.encrypt=false;
				send(new EncryptAuth(this));
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        } else {
            send(new JavascriptIncludes());
			send(new Connected());
			EventPublisher.raiseEvent(new ConnectEvent(this), null);
        }
        /*Log.debug(session.getUpgradeRequest().getHeaders("X-Real-IP")
                + " connected!", this.getClass());*/
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable t) {
        Log.error(t.getMessage(), this.getClass());
    }

    public void send(String data) {
        queue.add(data);
        try{
            throw new Exception();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void send(Response r) {
        OOnPage.newPage(this, r);
        try {
            queue.add(SynloadFramework.ow.writeValueAsString(r));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try{
            throw new Exception();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void send(Data r) {
        try {
            queue.add(SynloadFramework.ow.writeValueAsString(r));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try{
            throw new Exception();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class HandleRequest implements Runnable {
        WSHandler handler;
        Request request;

        public HandleRequest(WSHandler handler, Request request) {
            this.request = request;
            this.handler = handler;
        }

        public void run() {
            try {
                WSRouting.page(this.handler, this.request);
            } catch (IOException e) {
                if (SynloadFramework.debug) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class sendingThread implements Runnable {
        private WSHandler ws = null;

        public sendingThread(WSHandler ws) {
            this.ws = ws;
        }

        public void run() {
            while (true) {
                try {
                    if (ws.queue.size() > 0) {
                        List<String> queueTemp = new ArrayList<String>(ws.queue);

                        Iterator<String> queueIterator = queueTemp.iterator();
                        int c=0;
                        while (queueIterator.hasNext()) {
                            ws.isSending = true;
                            if (ws.encrypt) {
                                ws.session.getRemote().sendString(
                                     pki.encrypt(
                                         SynloadFramework.ow.writeValueAsString(queueIterator.next()),
                                         ws.getPki().getClientPublicKey()
                                     ),
                                     new verifySend(ws)
                                 );
                            } else {
                                String msg = queueIterator.next();
                                System.out.println("sent: "+msg.substring(0,30));
                                ws.session.getRemote().sendString(
                                        msg,
                                    new verifySend(ws)
                                );
                            }
                            if(ws.queue.size()>c){
                            	ws.queue.remove(c);
                            }
                            c++;
                        }
                    }
                    Thread.sleep(20);
                } catch (Exception e) {
                    if (SynloadFramework.debug) {
                        e.printStackTrace();
                    }
                    ws.session.close();
                    return; // ws session died, close down.
                }
            }
        }
    }

    public List<String> getFlags() {
		return flags;
	}

	public void setFlags(List<String> flags) {
		this.flags = flags;
	}

	/*
     * COPIED CODE FROM
     * http://stackoverflow.com/questions/14622622/generating-a-
     * random-hex-string-of-length-50-in-java-me-j2me
     */
    public static String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

    public class verifySend implements WriteCallback {
        private WSHandler ws = null;

        public verifySend(WSHandler ws) {
            this.ws = ws;
        }

        public void writeFailed(Throwable arg0) {
            this.ws.isSending = false;
            System.out.println("Failed to send");
        }

        public void writeSuccess() {
            this.ws.isSending = false;
            System.out.println("Successfully sent");
        }
    }

    @OnWebSocketMessage
    public void onWebSocketText(String message) {
        System.out.println(message.substring(0,30));
        ObjectMapper mapper = new ObjectMapper();
        try {
            Request request = null;
            if (encrypt) {
				request = mapper.readValue(pki.decrypt(message, pki.getServerPrivateKey()), Request.class);
            } else {
                request = mapper.readValue(message, Request.class);
            }
            (new Thread(new HandleRequest(this, request))).start();
        } catch (IOException e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
        // System.out.println("[DEBUG]["+session.getUpgradeRequest().getHeaders("X-Real-IP")+"] "
        // + message);
    }
}