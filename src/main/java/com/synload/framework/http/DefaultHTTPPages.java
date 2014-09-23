package com.synload.framework.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.FileUploadEvent;
import com.synload.framework.SynloadFramework;

public class DefaultHTTPPages {
	public void getIndex(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String[] URI) throws IOException{
		HTTPRouting.openFile("pages/index.html",response,baseRequest);
	}
	public void handleUploads(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String[] URI) throws IOException{
		request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, HTTPHandler.MULTI_PART_CONFIG);
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        if(!baseRequest.getParameterMap().containsKey("key")){
        	response.getWriter().println("{\"e\":\"no reference key provided\"}");
        	return;
		}
        try{
        	List<String> entry = new ArrayList<String>();
			for(Part part :request.getParts()){
				if(part.getSubmittedFileName()!=null){
					if(part.getSize()>0){
						if(SynloadFramework.maxUploadSize<=part.getSize()){
							response.getWriter().println("{\"e\":\"File size too large!\"}");
							part.delete();
							break;
						}
						String tempFile = SynloadFramework.randomString(32)+".file";
						try {
							InputStream is = part.getInputStream();
							OutputStream out = new FileOutputStream(SynloadFramework.uploadPath+tempFile);
							int bytesRead;
							byte[] buffer = new byte[8 * 1024];
							while ((bytesRead = is.read(buffer)) != -1) {
								out.write(buffer, 0, bytesRead);
							}
							out.close();
							is.close();
							part.delete();
						} catch (IOException e) {
							if(SynloadFramework.debug){
								e.printStackTrace();
							}
						}
						UploadedFile uf = new UploadedFile(part.getSubmittedFileName(),SynloadFramework.uploadPath,tempFile,part.getSize());
						EventPublisher.raiseEventThread(new FileUploadEvent(uf, StringUtils.join(baseRequest.getParameterMap().get("key"))),true);
						entry.add(uf.getName());
					}
				}
			}
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			response.getWriter().println(ow.writeValueAsString(entry));
        } catch (NullPointerException | ServletException e) {
        	if(SynloadFramework.debug){
        		e.printStackTrace();
        	}
        	response.getWriter().println("{\"e\":\"Authentication failed!\"}");
			try {
				for(Part part :request.getParts()){
					part.delete();
				}
			} catch (ServletException e1) {
				e1.printStackTrace();
			}
		}
	}
}
