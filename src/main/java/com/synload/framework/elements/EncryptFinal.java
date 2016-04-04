package com.synload.framework.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.util.Base64;

import com.synload.framework.handlers.Response;
import com.synload.framework.security.PKI;
import com.synload.framework.ws.WSHandler;

import sun.misc.BASE64Encoder;

public class EncryptFinal extends Response {
	public EncryptFinal(WSHandler ws) throws IOException {
    	this.setCallEvent("encryption_handshake_two");
        Map<String, String> tmp = new HashMap<String, String>();
	        BASE64Encoder encoder = new BASE64Encoder();
	    	String publicString = encoder.encode(ws.getPki().getServerPublicKey().getEncoded());
	        tmp.put("spk", publicString);
        this.setData(tmp);
    }
}
