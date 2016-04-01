package com.synload.framework.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.net.util.Base64;

import com.synload.framework.Log;
import com.synload.framework.handlers.Response;
import com.synload.framework.security.PKI;

public class EncryptAuth extends Response {
    public EncryptAuth(PKI pki) throws IOException {
    	this.setCallEvent("ecryption_handshake");
        Map<String, String> tmp = new HashMap<String, String>();
        	String publicString = Base64.encodeBase64String(pki.getServerPublicKey().getEncoded());
            tmp.put("spk", publicString);
        	tmp.put("test", pki.encrypt("test", pki.getServerPrivateKey()));
        this.setData(tmp);
    }
}