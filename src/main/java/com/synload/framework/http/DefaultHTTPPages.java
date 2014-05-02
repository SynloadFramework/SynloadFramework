package com.synload.framework.http;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

public class DefaultHTTPPages {
	public void getIndex(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String[] URI) throws IOException{
		HTTPRouting.openFile("pages/index.html",response,baseRequest);
	}
}
