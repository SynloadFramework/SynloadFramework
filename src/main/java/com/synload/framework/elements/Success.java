package com.synload.framework.elements;



import com.synload.framework.handlers.Response;

public class Success extends Response{
	public Success(String reference){
		this.setCallEvent(reference);
	}
}