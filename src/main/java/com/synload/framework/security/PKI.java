package com.synload.framework.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;

import com.synload.eventsystem.events.annotations.Event;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.ConnectEvent;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Connected;
import com.synload.framework.elements.EncryptAuth;
import com.synload.framework.elements.EncryptFinal;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.framework.ws.annotations.WSEvent;

public class PKI {
    private PrivateKey serverPrivateKey = null;
    private PublicKey serverPublicKey = null;
    private X509EncodedKeySpec keySpec;
    private PublicKey clientPublicKey = null;
    public PKI(){
    	generateKeys();
    }
    public void sendKeysToPubKeyServers(String publicKey) throws NoSuchAlgorithmException{
    	MessageDigest md = MessageDigest.getInstance("SHA-512");
    	byte[] digestBytes = md.digest(publicKey.getBytes(StandardCharsets.UTF_8));
    	StringBuilder hexString = new StringBuilder();
    	for (byte b : digestBytes) {
    	    hexString.append(String.format("%02x", b));
    	}
    	String sha512 = hexString.toString();
    	HttpClient httpClient = HttpClients.custom().build();
    	for( HashMap<String, String> server : SynloadFramework.pubkeyServers ){
	    	RequestBuilder reqBuilder = RequestBuilder.post().setUri("http://"+server.get("address")+"/auth.php");
			reqBuilder.addParameter("key", sha512);
			reqBuilder.addParameter("username",server.get("username"));
			reqBuilder.addParameter("password",server.get("password"));
			HttpUriRequest post = reqBuilder.build();
			try {
				CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(post);
				HttpEntity entity = response.getEntity();
				String content;
				try (InputStream is = entity.getContent()) {
				    ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    byte[] buffer = new byte[4096];
				    int bytesRead;
				    while ((bytesRead = is.read(buffer)) != -1) {
				        baos.write(buffer, 0, bytesRead);
				    }
				    content = baos.toString(StandardCharsets.UTF_8.name());
				}
				if(content.equals("OK")){
					Log.info("Sent "+server.get("address")+" the key hash", PKI.class);
				}else{
					Log.error("Not Working", PKI.class);
				}
				response.close();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    public void generateKeys(){
    	KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(SynloadFramework.getEncryptLevel());
			KeyPair kp = kpg.genKeyPair();
			this.setServerPublicKey(kp.getPublic());
			this.setServerPrivateKey(kp.getPrivate());
			//Log.info(Base64.getEncoder().encodeToString(this.getServerPublicKey().getEncoded()), PKI.class);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    }
    
    @WSEvent(name = "ReceiveClientPublicKey", description = "Receive Client Public Key", action = "cpk", enabled = true, method = "synfam")
    public void receiveClientPub(RequestEvent event) throws JsonProcessingException, IOException {
    	if(event.getRequest().getData().containsKey("cpk")){
    		Security.addProvider(new BouncyCastleProvider());
    		try {
    			String cpk = event.getRequest().getData().get("cpk");
    			byte[] pubKey = Base64.getDecoder().decode(
    				decrypt(
						cpk,
						event.getSession().getPki().getServerPrivateKey()
					)
				);
    			
    			//System.out.println(decrypt(cpk, event.getSession().getPki().getServerPrivateKey()));
    			
        		keySpec = new X509EncodedKeySpec(pubKey);
    			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    			
    			event.getSession().getPki().setClientPublicKey(keyFactory.generatePublic(keySpec));
    			
    			//System.out.println(Base64.encodeBase64String(event.getSession().getPki().getClientPublicKey().getEncoded()));
    			
				event.getSession().encrypt=true;
				this.generateKeys();
				event.getSession().send(new EncryptFinal(event.getSession()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
    	}
    }
    
    @WSEvent(name = "ReceiveClientPublicKey", description = "Receive Client Public Key", action = "ack", enabled = true, method = "synfam")
    public void receiveClientAcknowledge(RequestEvent event) throws JsonProcessingException, IOException {
    	if(event.getRequest().getData().containsKey("message")){
			String message = event.getRequest().getData().get("message");
			if(message.equals("HELLO")){
				EventPublisher.raiseEvent(new ConnectEvent(event.getSession()), true, null);
				event.getSession().send(new Connected());
			}
    	}
    }
	@Event(name="UserConnect", description = "triggered on user connection", enabled = true)
	public void onConnect(ConnectEvent ce){
		if(ce.getSession().encrypt==true){
			System.out.println("User connected on secure line");
		}
	}
    public String encrypt(String data, Key key) {
    	String rdata = "";
        byte[] cipherText = null;
        //System.out.println(Base64.encodeBase64String(key.getEncoded()));
    	Base64.Encoder encoder = Base64.getEncoder();
    	try {
    		Pattern regex = Pattern.compile("((.|[\r\n]){1,50})");
    		Matcher regexMatcher = regex.matcher(data);
    		while (regexMatcher.find()) {
    			String toEn = regexMatcher.group(1);
    			//System.out.println(toEn);
        		final Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, key);
			    cipherText = cipher.doFinal(encoder.encodeToString(toEn.getBytes(StandardCharsets.UTF_8)).getBytes());
				if(rdata.equals("")){
					rdata=encoder.encodeToString(cipherText);
				}else{
					rdata=rdata+"&"+encoder.encodeToString(cipherText);
				}
    		} 
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
        return rdata;
    }

    public String decrypt(String data, Key key){
    	String decryptedText = "";
    	//System.out.println(Base64.encodeBase64String(key.getEncoded()));
        try {
        	Base64.Decoder decoder = Base64.getDecoder();
        	for(String toEn: data.split("&")){
        		final Cipher cipher = Cipher.getInstance("RSA");
        		cipher.init(Cipher.DECRYPT_MODE, key);
        		if(decryptedText.equals("")){
        			decryptedText = new String(cipher.doFinal(decoder.decode(toEn)));
        		}else{
        			decryptedText = decryptedText + new String(cipher.doFinal(decoder.decode(toEn)));
        		}
        	}
        } catch (Exception ex) {
    		ex.printStackTrace();
        }
        return decryptedText;
    }
	public Key getServerPrivateKey() {
		return serverPrivateKey;
	}
	public void setServerPrivateKey(PrivateKey serverPrivateKey) {
		this.serverPrivateKey = serverPrivateKey;
	}
	public Key getServerPublicKey() {
		return serverPublicKey;
	}
	public void setServerPublicKey(PublicKey serverPublicKey) {
		this.serverPublicKey = serverPublicKey;
	}
	public Key getClientPublicKey() {
		return clientPublicKey;
	}
	public void setClientPublicKey(PublicKey clientPublicKey) {
		this.clientPublicKey = clientPublicKey;
	}
    
}
