package com.synload.framework.http;

import java.io.IOException;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import com.synload.framework.SynloadFramework;


public class HTTPHandler extends ContextHandler{
	public static final MultipartConfigElement MULTI_PART_CONFIG = 
			new MultipartConfigElement(
				"/tmp/", 
				943718400, 
				948718400, 
				948718400
			);
	public HTTPHandler(){
		this.setMaxFormContentSize(2000000000);
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response){
		baseRequest.setAsyncSupported(true);
		if(SynloadFramework.bannedIPs.contains(baseRequest.getRemoteAddr())){
			response.setContentType("text/html;charset=utf-8");
    		response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			baseRequest.setHandled(true);
			return;
		}
		/*if(baseRequest.getHeader("Connection")!=null){
			response.setHeader( "Connection", "Keep-Alive");
		}*/
		if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
			baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		}
		this.setStopTimeout(60000);
		try {
			HTTPRouting.page(target, baseRequest, request, response);
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
}