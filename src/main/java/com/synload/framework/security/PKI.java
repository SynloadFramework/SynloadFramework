package com.synload.framework.security;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import org.apache.commons.net.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.EncryptAuth;
import com.synload.framework.elements.EncryptFinal;
import com.synload.framework.elements.FullPage;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;

public class PKI {
    private Key serverPrivateKey = null;
    private Key serverPublicKey = null;
    private Key clientPublicKey = null;
    public PKI(){
    	generateKeys();
    	clientPublicKey = serverPublicKey;
    }
    public void generateKeys(){
    	KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(SynloadFramework.getEncryptLevel());
			KeyPair kp = kpg.generateKeyPair();
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
    				Base64.decodeBase64(decrypt(cpk))
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
    public String encrypt(String data) {
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
          cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
          cipherText = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
          e.printStackTrace();
        }
        return Base64.encodeBase64String(cipherText);
    }

    public String decrypt(String data){
        /*String passphrase = encryptKey;
        AesUtil aesUtil = new AesUtil(128, 1000);
        return aesUtil.decrypt(salt, iv, passphrase, data);*/
    	byte[] dectyptedText = null;
        try {
          final Cipher cipher = Cipher.getInstance("RSA");
          cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey);
          dectyptedText = cipher.doFinal(Base64.decodeBase64(data));
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
	public void setServerPrivateKey(Key serverPrivateKey) {
		this.serverPrivateKey = serverPrivateKey;
	}
	public Key getServerPublicKey() {
		return serverPublicKey;
	}
	public void setServerPublicKey(Key serverPublicKey) {
		this.serverPublicKey = serverPublicKey;
	}
	public Key getClientPublicKey() {
		return clientPublicKey;
	}
	public void setClientPublicKey(Key clientPublicKey) {
		this.clientPublicKey = clientPublicKey;
	}
    
}
