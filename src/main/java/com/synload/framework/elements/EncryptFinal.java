package com.synload.framework.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.util.Base64;

import com.synload.framework.handlers.Response;
import com.synload.framework.security.PKI;

public class EncryptFinal extends Response {
	public EncryptFinal(PKI pki) throws IOException {
    	this.setCallEvent("ecryption_handshake");
        Map<String, String> tmp = new HashMap<String, String>();
        	tmp.put("spk", Base64.encodeBase64String(pki.getServerPublicKey().getEncoded()));
        this.setData(tmp);
    }
}
