package com.synload.framework.ws;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.backgrounds.SquigglesBackgroundProducer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.gimpy.ShearGimpyRenderer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;

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
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.CloseEvent;
import com.synload.eventsystem.events.ConnectEvent;
import com.synload.framework.Log;
import com.synload.framework.OOnPage;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.EncryptAuth;
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
	public boolean encrypt = false;
	public String encryptKey = "";
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
		Log.debug("Close: statusCode=" + statusCode + ", reason=" + reason,this.getClass());
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
		sendingThreadVar = (new Thread(new sendingThread(this)));
		sendingThreadVar.start();
		SynloadFramework.clients.add(this);
		if(SynloadFramework.isEncryptEnabled()){
			Captcha c = generateCaptcha();
			try {
				send(new EncryptAuth(c));
			} catch (IOException e) {
				e.printStackTrace();
			}
			encryptKey = c.getAnswer();
		}else{
			if(SynloadFramework.isSiteDefaults()){
				send(new JavascriptIncludes());
			}
		}
		Log.debug(session.getUpgradeRequest().getHeaders("X-Real-IP")+" connected!",this.getClass());
        //System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        EventPublisher.raiseEvent(new ConnectEvent(this), null);
	}
	
	public Captcha generateCaptcha(){
		Captcha captcha = new Captcha.Builder(800, 300)
			.addBackground(new SquigglesBackgroundProducer())
			.addText(new DefaultTextProducer(13))
			.addNoise()
			.gimp(new FishEyeGimpyRenderer())
			.addNoise()
			.addNoise()
			.addNoise(new CurvedLineNoiseProducer())
			.build();
		return captcha;
	}
	
	@OnWebSocketError
	public void onWebSocketError(Throwable t) {
		Log.error(t.getMessage(),this.getClass());
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
		private WSHandler ws = null;
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
							if(ws.encrypt){
								ws.session.getRemote().sendString(
									encrypt(SynloadFramework.ow.writeValueAsString(queueIterator.next()), getRandomHexString(64), getRandomHexString(32)),
									new verifySend(ws)
								);
							}else{
								ws.session.getRemote().sendString(queueIterator.next(),new verifySend(ws));
								if(SynloadFramework.isEncryptEnabled()){
									ws.encrypt = true;
								}
							}
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
	/*
	 * COPIED CODE FROM http://stackoverflow.com/questions/14622622/generating-a-random-hex-string-of-length-50-in-java-me-j2me
	 * */
	private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
	public String encrypt(String data, String salt, String iv) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException{
		String passphrase = encryptKey;
		AesUtil aesUtil = new AesUtil(128, 1000);
		String eData = aesUtil.encrypt(salt, iv, passphrase, data);
		String enDat = eData+":"+salt+":"+iv;
		return enDat;
	}
	public String decrypt(String data, String salt, String iv) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, Base64DecodingException, IOException{
		String passphrase = encryptKey;
		AesUtil aesUtil = new AesUtil(128, 1000);
		return aesUtil.decrypt(salt, iv, passphrase, data);
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
			Request request = null;
			if(encrypt){
				String[] s = message.split(":");
				try {
					request = mapper.readValue(decrypt(s[0],s[1],s[2]), Request.class);
				} catch (InvalidKeyException | NoSuchAlgorithmException
						| InvalidKeySpecException
						| InvalidParameterSpecException
						| IllegalBlockSizeException | BadPaddingException
						| NoSuchPaddingException
						| InvalidAlgorithmParameterException
						| Base64DecodingException e) {
					e.printStackTrace();
				}
			}else{
				request = mapper.readValue(message, Request.class);
			}
			(new Thread(new HandleRequest(this,request))).start();
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
        //System.out.println("[DEBUG]["+session.getUpgradeRequest().getHeaders("X-Real-IP")+"] " + message);
	}
}