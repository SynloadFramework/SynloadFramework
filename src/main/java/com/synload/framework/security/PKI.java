package com.synload.framework.security;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.net.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Connected;
import com.synload.framework.elements.EncryptAuth;
import com.synload.framework.elements.EncryptFinal;
import com.synload.framework.elements.FullPage;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;

import junit.framework.Test;

public class PKI {
    private PrivateKey serverPrivateKey = null;
    private PublicKey serverPublicKey = null;
    private PublicKey clientPublicKey = null;
    public PKI(){
    	generateKeys();
    	clientPublicKey = serverPublicKey;
    }
    public void generateKeys(){
    	KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(SynloadFramework.getEncryptLevel());
			KeyPair kp = kpg.genKeyPair();
			serverPublicKey = kp.getPublic();
			serverPrivateKey = kp.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    }
    @Event(name = "RecieveClientPublicKey", description = "Recieve Client Public Key", trigger = { "synfam", "cpk" }, type = Type.WEBSOCKET)
    public void recieveClientPub(RequestEvent event) throws JsonProcessingException, IOException {
    	if(event.getRequest().getData().containsKey("cpk")){
    		try {
    			String cpk = event.getRequest().getData().get("cpk");
        		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
    				Base64.decodeBase64(decrypt(cpk,event.getSession().getPki().getServerPrivateKey()))
    			);
    			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				clientPublicKey = keyFactory.generatePublic(keySpec);
				event.getSession().encrypt=true;
				generateKeys();
				event.getSession().send(new EncryptFinal(this));
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
    	}
    }
    @Event(name = "RecieveClientPublicKey", description = "Recieve Client Public Key", trigger = { "synfam", "ack" }, type = Type.WEBSOCKET)
    public void recieveClientAcknowledge(RequestEvent event) throws JsonProcessingException, IOException {
    	if(event.getRequest().getData().containsKey("message")){
			String message = event.getRequest().getData().get("message");
			String data = event.getSession().getPki().decrypt(message, event.getSession().getPki().getServerPrivateKey());
			if(data.equals("test")){
				event.getSession().encrypt=true;
				event.getSession().send(new Connected());
			}
    	}
    }
    public String encrypt(String data, Key key) {
        /*String passphrase = encryptKey;
        AesUtil aesUtil = new AesUtil(128, 1000);
        String eData = aesUtil.encrypt(salt, iv, passphrase, data);
        String enDat = eData + ":" + salt + ":" + iv;
        return enDat;*/
        byte[] cipherText = null;
        try {
          // get an RSA cipher object and print the provider
          final Cipher cipher = Cipher.getInstance("RSA");
          // encrypt the plain text using the public key
          cipher.init(Cipher.ENCRYPT_MODE, key);
          cipherText = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
          e.printStackTrace();
        }
        return Hex.encodeHexString(cipherText);
    }

    public String decrypt(String data, Key key){
        /*String passphrase = encryptKey;
        AesUtil aesUtil = new AesUtil(128, 1000);
        return aesUtil.decrypt(salt, iv, passphrase, data);*/
    	byte[] dectyptedText = null;
        try {
          final Cipher cipher = Cipher.getInstance("RSA");
          cipher.init(Cipher.DECRYPT_MODE, key);
          dectyptedText = cipher.doFinal(Hex.decodeHex(data.toCharArray()));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        return new String(dectyptedText);
    }
    public Response generate() throws IOException{
    	return new EncryptAuth(this);
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
