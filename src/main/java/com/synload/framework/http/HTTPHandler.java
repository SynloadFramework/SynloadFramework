package com.synload.framework.http;

import java.io.IOException;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.synload.framework.SynloadFramework;


public class HTTPHandler extends ContextHandler{
	private static final MultipartConfigElement MULTI_PART_CONFIG = 
			new MultipartConfigElement(
				"./uploads/", 
				943718400, 
				948718400, 
				948718400
			);
	public HTTPHandler(){
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		if(SynloadFramework.bannedIPs.contains(baseRequest.getRemoteAddr())){
			response.setContentType("text/html;charset=utf-8");
    		response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			baseRequest.setHandled(true); 
			return;
		}
		if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
			baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		}
		HTTPRouting.page(target, baseRequest, request, response);
	}
}