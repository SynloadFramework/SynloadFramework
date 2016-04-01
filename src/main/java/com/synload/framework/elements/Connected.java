package com.synload.framework.elements;

import com.synload.framework.handlers.Response;

public class Connected extends Response {
	public Connected(){
    	this.setCallEvent("conn_est");
    }
}
