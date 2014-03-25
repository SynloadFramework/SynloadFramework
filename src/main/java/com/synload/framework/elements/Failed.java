package com.synload.framework.elements;

import com.synload.framework.handlers.Response;

public class Failed extends Response{
	public Failed(String reference){
		this.setCallEvent(reference);
	}
}