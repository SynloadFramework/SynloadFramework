package com.synload.framework.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.WebEvent;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.http.modules.HTTPResponse;
import com.synload.framework.modules.ModuleLoader;

public class HTTPRouting {
    public static HashMap<String, HTTPResponse> routes = new HashMap<String, HTTPResponse>();

    public static HashMap<String, HTTPResponse> getRoutes() {
        return routes;
    }

    public static boolean addRoutes(String path, HTTPResponse response) {
    	if(response==null || path.equals("")){
    		return false;
    	}
        if (HTTPRouting.routes.containsKey(path)) {
            return false;
        } else {
            HTTPRouting.routes.put(path, response);
            if (HTTPRouting.routes.containsKey(path)) {
                return true;
            } else {
                return false;
            }
        }
    }

    @SuppressWarnings("unused")
    public static boolean openFile(String filename, HttpServletResponse response, Request baseRequest) throws IOException {
        boolean properFile = true;
        String mime = "text/html;charset=utf-8";
        try {
            // properFile =
            // filename.split("/")[filename.split("/").length-1].matches("(?sim)([a-z.A-Z0-9]+)");
        } catch (PatternSyntaxException ex) {
        }
        String ext = filename.split("(?sim)\\.")[filename.split("(?sim)\\.").length - 1];
        if (ext.equalsIgnoreCase("js")) {
            mime = "application/javascript";
        } else if (ext.equalsIgnoreCase("css")) {
            mime = "text/css";
        } else if (ext.equalsIgnoreCase("webm")) {
            mime = "video/webm";
        } else if (ext.equalsIgnoreCase("mp4")) {
            mime = "video/mp4";
        } else if (ext.equalsIgnoreCase("jpg")) {
            mime = "image/jpeg";
        } else if (ext.equalsIgnoreCase("png")) {
            mime = "image/png";
        } else if (ext.equalsIgnoreCase("ico")) {
            mime = "image/x-icon";
        }
        if (properFile) {
            boolean htmlExists = (new File(filename)).exists();
            if (htmlExists) {
                baseRequest.setHandled(true);
                String range = baseRequest.getHeader("Range");
                if (baseRequest.getParameterMap().containsKey("p")) {
                    range = "bytes="
                            + baseRequest.getParameterMap().get("p")[0];
                }
                boolean isCached = false;
                HashMap<String, Object> htmlf = null;
                if (SynloadFramework.htmlFiles.containsKey(filename)) {
                    htmlf = SynloadFramework.htmlFiles.get(filename);
                    isCached = htmlf.get("modified").equals(
                            (new File(filename)).lastModified());
                }
                if (range == null) {
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("Accept-Ranges", "bytes");
                    response.setContentType(mime);
                    response.setContentLengthLong((new File(filename)).length());
                    if (!isCached) {
                        File htmlFile = (new File(filename));
                        InputStream is = new FileInputStream(htmlFile);
                        long bytesRead = 0;
                        boolean cache = false;
                        if ((new File(filename)).length() < 200000) {
                            // cache = true;
                        }
                        byte[] buffer = new byte[8 * 1024];
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int byteArrayPos = 0;
                        while (is.read(buffer) != -1) {
                            if (cache) {
                                outputStream.write(buffer);
                                outputStream.flush();
                            }
                            try {
                                if (ext.equalsIgnoreCase("html")
                                        || ext.equalsIgnoreCase("css")
                                        || ext.equalsIgnoreCase("js")) {
                                    response.getWriter().print(
                                            new String(buffer));
                                    response.getWriter().flush();
                                } else if (ext.equalsIgnoreCase("mp4")
                                        || ext.equalsIgnoreCase("avi")
                                        || ext.equalsIgnoreCase("webm")
                                        || ext.equalsIgnoreCase("jpg")
                                        || ext.equalsIgnoreCase("png")
                                        || ext.equalsIgnoreCase("ico")
                                        || ext.equalsIgnoreCase("gif")) {
                                    response.getOutputStream().write(buffer);
                                    response.getOutputStream().flush();
                                }
                            } catch (IOException ex) {
                                if (SynloadFramework.debug) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        if (cache) {
                            HashMap<String, Object> tmpf = new HashMap<String, Object>();
                            tmpf.put("modified",
                                    (new File(filename)).lastModified());
                            tmpf.put("data", outputStream.toByteArray());
                            SynloadFramework.htmlFiles.put(filename, tmpf);
                        }
                        is.close();
                        return true;
                    } else {
                        response.getOutputStream().write(
                                (byte[]) htmlf.get("data"));
                        return true;
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    response.setContentType(mime);
                    String[] bytesParts = null;
                    try {
                        Pattern regex = Pattern
                                .compile("bytes=([0-9\\-, ]+)",
                                        Pattern.CASE_INSENSITIVE
                                                | Pattern.UNICODE_CASE);
                        Matcher regexMatcher = regex.matcher(range);
                        if (regexMatcher.find()) {
                            bytesParts = regexMatcher.group(1).split(",");
                        }
                    } catch (PatternSyntaxException ex) {
                    }
                    String outerContentRange = "";
                    long totalSize = 0;
                    if (bytesParts != null) {
                        for (String part : bytesParts) {
                            if (!part.equals("")) {
                                String[] partData = part.trim().split("-");
                                if (partData.length == 2) {
                                    if (partData[1] != null
                                            && !partData[1].equals("")) {
                                        totalSize += Long.valueOf(partData[1])
                                                - Long.valueOf(partData[0]);
                                        outerContentRange = Long
                                                .valueOf(partData[0])
                                                + "-"
                                                + (Long.valueOf(partData[1]) - 1);
                                    } else {
                                        totalSize += (new File(filename))
                                                .length()
                                                - Long.valueOf(partData[0]);
                                        outerContentRange = Long
                                                .valueOf(partData[0])
                                                + "-"
                                                + ((new File(filename))
                                                        .length() - 1);
                                    }
                                } else {
                                    totalSize += (new File(filename)).length()
                                            - Long.valueOf(partData[0]);
                                    outerContentRange = Long
                                            .valueOf(partData[0])
                                            + "-"
                                            + ((new File(filename)).length() - 1);
                                }
                            }
                        }
                    }
                    response.setContentLengthLong(totalSize);
                    response.setHeader("Accept-Ranges", "bytes");
                    response.setHeader("Content-Range",
                            "bytes " + outerContentRange + "/"
                                    + (new File(filename)).length());
                    if (bytesParts != null) {
                        for (String part : bytesParts) {
                            if (!part.equals("")) {
                                String[] partData = part.trim().split("-");
                                if (partData.length == 2) {
                                    if (partData[1] != null
                                            && !partData[1].equals("")) {
                                        sendData(new File(filename),
                                                response.getOutputStream(),
                                                Long.valueOf(partData[0]),
                                                Long.valueOf(partData[1]));
                                    } else {
                                        sendData(new File(filename),
                                                response.getOutputStream(),
                                                Long.valueOf(partData[0]),
                                                (new File(filename)).length());
                                    }
                                } else {
                                    sendData(new File(filename),
                                            response.getOutputStream(),
                                            Long.valueOf(partData[0]),
                                            (new File(filename)).length());
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void sendData(File file, ServletOutputStream stream,
            long minimum, long maximum) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] buffer = new byte[8 * 1024];
            long bytesRead = 0, readBytes = 8 * 1024;
            int cc = 0;
            while (cc != -1) {
                cc = is.read(buffer);
                bytesRead += cc;
                if (bytesRead >= minimum && (bytesRead - readBytes) <= minimum) {
                    int max = (int) (bytesRead - (bytesRead - readBytes));
                    if (bytesRead > maximum) {
                        max = (int) (maximum - (bytesRead - readBytes));
                    }
                    int min = (int) (minimum - (bytesRead - readBytes));
                    for (int i = min; i < max; i++) {
                        stream.write(buffer[i]);
                    }
                } else if (bytesRead > minimum) {
                    if (bytesRead > maximum
                            && (bytesRead - readBytes) <= maximum) {
                        long max = maximum - (bytesRead - readBytes);
                        for (int i = 0; i < max; i++) {
                            stream.write(buffer[i]);
                        }
                    } else if (bytesRead < maximum) {
                        stream.write(buffer);
                    }
                }
                stream.flush();
            }
        } catch (IOException e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                if (SynloadFramework.debug) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    public static void sendResource(String filename, byte[] resource, HttpServletResponse response){
    	if(filename==null || resource==null || response==null){
    		return;
    	}
    	String mime = "text/html;charset=utf-8";
        String ext = filename.split("(?sim)\\.")[filename.split("(?sim)\\.").length - 1];
        if (ext.equalsIgnoreCase("js")) {
            mime = "application/javascript";
            response.setCharacterEncoding("UTF-8");
        } else if (ext.equalsIgnoreCase("css")) {
            mime = "text/css";
            response.setCharacterEncoding("UTF-8");
        } else if (ext.equalsIgnoreCase("webm")) {
            mime = "video/webm";
        } else if (ext.equalsIgnoreCase("mp4")) {
            mime = "video/mp4";
        } else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
            mime = "image/jpeg";
        } else if (ext.equalsIgnoreCase("png")) {
            mime = "image/png";
        } else if (ext.equalsIgnoreCase("gif")) {
            mime = "image/gif";
        } else if (ext.equalsIgnoreCase("ico")) {
            mime = "image/x-icon";
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Accept-Ranges", "bytes");
        response.setContentType(mime);
        response.setContentLengthLong(resource.length);
        try {
			response.getOutputStream().write(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /*
     * Handle HTTP requests, called whenever an http request is received 
     */
    public static boolean page(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] URI = target.split("/",-1);
        //Log.info("Request received: "+target, HTTPRouting.class);
        if(URI.length>1){
        	if (URI[1].equalsIgnoreCase("ws")) {
                return false;
            }else if(ModuleLoader.resources.containsKey(URI[1])){
        		if(ModuleLoader.resources.get(URI[1]).containsKey(target.replace("/"+URI[1]+"/", ""))){
        			sendResource(target.replace("/"+URI[1], ""),ModuleLoader.resources.get(URI[1]).get(target.replace("/"+URI[1]+"/", "")), response);
        			return true;
        		}
        	}
        }
        //Log.info("Request received: "+baseRequest.getMethod(), HTTPRouting.class);
        for (Entry<String, HTTPResponse> httpResponse : HTTPRouting.routes.entrySet()) {
        	String path = httpResponse.getKey();
        	//Log.info("Request checked: "+httpResponses.getValue().getHttpMethod(), HTTPRouting.class);
        	//Log.info(target+":"+baseRequest.getMethod()+"="+path+":"+httpResponse.getValue().getHttpMethod().toLowerCase(), HTTPRouting.class);
            if (target.matches(path) && baseRequest.getMethod().toLowerCase().equals(httpResponse.getValue().getHttpMethod().toLowerCase())) {
                try {
                    HTTPResponse p = httpResponse.getValue();
                    if(!p.getMimetype().isEmpty()){
	                    response.setContentType(p.getMimetype());
	                    response.setStatus(HttpServletResponse.SC_OK);
                    }
                    p.getListener().getMethod(
                		p.getMethod(),
                		HttpRequest.class
            		).invoke(
        				p.getListener().newInstance(), 
        				new HttpRequest(target, baseRequest, request, response, URI)
    				);
                    return true;
                } catch (Exception e) {
                    if (SynloadFramework.debug) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        if (URI.length == 2) {
            if (URI[1].equalsIgnoreCase("ws")) {
            	return false;
            }
        }
        
        boolean file = false;
        if (URI.length == 3) {
            try {
                Pattern regex = Pattern.compile("\\.\\.",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                                | Pattern.MULTILINE);
                Matcher regexMatcher = regex.matcher(URI[1] + "/" + URI[2]);
                file = regexMatcher.find();
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }
        }
        if (URI.length == 3) {
            // System.out.println("[WB][I] Checking if route is a file <"+URI[1]+"><"+URI[2]+">!");
            if ((new File(URI[1] + "/" + URI[2])).exists() && !file) {
                System.out.println("[WB][I] Sending file data <" + URI[1]
                        + "><" + URI[2] + ">!");
                openFile(URI[1] + "/" + URI[2], response, baseRequest);
                return true;
            }
        }
        // System.out.println("[WB][I] File not found for route sending to modules!");
        EventPublisher.raiseEvent(new WebEvent(target, baseRequest, request,
                response, URI), target);
		return false;
    }
}