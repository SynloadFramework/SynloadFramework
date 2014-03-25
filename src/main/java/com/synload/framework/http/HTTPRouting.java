package com.synload.framework.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.WebEvent;
import com.synload.framework.SynloadFramework;

public class HTTPRouting{
	public static HashMap<String, HTTPResponse> routes = new HashMap<String, HTTPResponse>();
	public static HashMap<String, HTTPResponse> getRoutes() {
		return routes;
	}
	public static boolean addRoutes(String path, HTTPResponse response) {
		if(HTTPRouting.routes.containsKey(path)){
			return false;
		}else{
			HTTPRouting.routes.put(path, response);
			if(HTTPRouting.routes.containsKey(path)){
				return true;
			}else{
				return false;
			}
		}
	}
	@SuppressWarnings("unused")
	public static boolean openFile(String Path,String filename,HttpServletResponse response, Request baseRequest) throws IOException{
		boolean properFile = false;
		String mime = "text/html;charset=utf-8";
        try {
        	properFile = filename.matches("(?sim)([a-z.A-Z0-9]+)");
        } catch (PatternSyntaxException ex) {
        	ex.printStackTrace();
        }
        String ext = filename.split("(?sim)\\.")[1];
		if(ext.equalsIgnoreCase("js")){
			mime = "application/javascript";
		}else if(ext.equalsIgnoreCase("css")){
			mime = "text/css";
		}
        if(properFile){
        	boolean htmlExists = ( new File(Path+"/"+filename)).exists();
        	if(htmlExists){
        		response.setContentType(mime);
        		response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true); 
        		boolean isCached = false;
        		HashMap<String, Object> htmlf = null;
        		if(SynloadFramework.htmlFiles.containsKey(Path+"/"+filename)){
        			htmlf = SynloadFramework.htmlFiles.get(Path+"/"+filename);
        			isCached = htmlf.get("modified").equals(( new File(Path+"/"+filename)).lastModified());
        		}
        		if(!isCached){
	        		File htmlFile = ( new File(Path+"/"+filename));
	        		InputStream is = new FileInputStream(htmlFile);
	        		HashMap<String, Object> tmpf = new HashMap<String, Object>(); 
	        		tmpf.put("modified", ( new File(Path+"/"+filename)).lastModified());
					int bytesRead;
	        		byte[] buffer = new byte[8 * 1024];
	        		String dataOut = "";
	        		while ((bytesRead = is.read(buffer)) != -1) {
	        			String dataM = new String(buffer);
	        			dataOut += dataM;
	        			response.getWriter().print(dataM.trim());
	        		}
	        		tmpf.put("data", dataOut);
	        		SynloadFramework.htmlFiles.put(Path+"/"+filename, tmpf);
	        		is.close();
	        		return true;
        		}else{
        			response.getWriter().print(((String)htmlf.get("data")).trim());
        			return true;
        		}
        	}
        }
        return false;
	}
	public static void page(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException{
		String[] URI = target.split("/");
		//System.out.println("[WB][I] Request recieved!");
		if(HTTPRouting.routes.containsKey(target)){	
			HTTPResponse p = HTTPRouting.routes.get(target);
			try {
				//System.out.println("[WB][I] Route found sending to method!");
				p.getListener().getMethod(
					p.getMethod(), String.class, Request.class, HttpServletRequest.class, HttpServletResponse.class, String[].class
				).invoke(
					p.getListener().newInstance(), target, baseRequest, request, response, URI
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			if(URI.length==2){
				if(URI[1].equalsIgnoreCase("ws")){
					return;
				}
			}
			//System.out.println("[WB][I] Route not found checking for files!!");
			boolean folder = false;
			try {
				Pattern regex = Pattern.compile("([a-zA-Z0-9._\\-()\\[\\] ]+)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher regexMatcher = regex.matcher(URI[1]);
				folder = regexMatcher.matches();
			} catch (PatternSyntaxException ex) {
			}
			boolean file = false;
			try {
				Pattern regex = Pattern.compile("([a-zA-Z0-9._\\-()\\[\\] ]+)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
				Matcher regexMatcher = regex.matcher(URI[2]);
				file = regexMatcher.matches();
			} catch (PatternSyntaxException ex) {
			}
			if(URI.length==3){
				//System.out.println("[WB][I] Checking if route is a file <"+URI[1]+"><"+URI[2]+">!");
				if((new File(URI[1]+"/"+URI[2])).exists() && folder && file){
					//System.out.println("[WB][I] Sending file data <"+URI[1]+"><"+URI[2]+">!");
					openFile(URI[1], URI[2], response, baseRequest);
					return;
				}
			}
			//System.out.println("[WB][I] File not found for route sending to modules!");
			EventPublisher.raiseEventThread(new WebEvent(target,baseRequest,request,response,URI),false);
		}
	}
}