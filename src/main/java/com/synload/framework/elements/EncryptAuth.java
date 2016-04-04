package com.synload.framework.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import sun.misc.BASE64Encoder;

public class EncryptAuth extends Response {
    public EncryptAuth(WSHandler ws) throws IOException {
    	this.setCallEvent("encryption_handshake");
        Map<String, String> tmp = new HashMap<String, String>();
        	BASE64Encoder encoder = new BASE64Encoder();
        	String publicString = encoder.encode(ws.getPki().getServerPublicKey().getEncoded());
        	//System.out.println(publicString);
            tmp.put("spk", publicString);
        this.setData(tmp);
    }
}