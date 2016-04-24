package com.synload.framework.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
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
import com.synload.framework.http.annotations.Get;
import com.synload.framework.http.annotations.MimeType;
import com.synload.framework.http.annotations.OnlyIf;
import com.synload.framework.http.annotations.Post;
import com.synload.framework.http.modules.UploadedFile;
import com.synload.framework.modules.ModuleLoader;

public class DefaultHTTPPages {
	
	@Get("/")
    public void getIndex(HttpRequest httpRequest) throws IOException {
        HTTPRouting.sendResource("index.html", ModuleLoader.resources.get("synloadframework").get("index.html"), httpRequest.getResponse());
    }
	
	@OnlyIf(property="enableUploads", is=true)
	@Post("/system/uploads")
	@MimeType("text/html;charset=utf-8")
    public void handleUploads(HttpRequest httpRequest) throws IOException {
		httpRequest.getRequest().setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, HTTPHandler.MULTI_PART_CONFIG);
		httpRequest.getBaseRequest().setHandled(true);
        if (!httpRequest.getBaseRequest().getParameterMap().containsKey("key")) {
        	httpRequest.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
        	httpRequest.getResponse().getWriter().println("{\"e\":\"no reference key provided\"}");
            return;
        }
        if (!httpRequest.getBaseRequest().getParameterMap().containsKey("user")) {
        	httpRequest.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
        	httpRequest.getResponse().getWriter().println("{\"e\":\"no user provided\"}");
            return;
        }

        try {
            List<String> entry = new ArrayList<String>();
            for (Part part : httpRequest.getRequest().getParts()) {
                if (part.getSubmittedFileName() != null) {
                    if (part.getSize() > 0) {
                        if (SynloadFramework.maxUploadSize <= part.getSize()) {
                        	httpRequest.getResponse().getWriter().println("{\"e\":\"File size too large!\"}");
                            part.delete();
                            break;
                        }
                        String tempFile = SynloadFramework.randomString(32)+ ".file";
                        try {
                            InputStream is = part.getInputStream();
                            OutputStream out = new FileOutputStream(SynloadFramework.uploadPath + tempFile);
                            int bytesRead;
                            byte[] buffer = new byte[8 * 1024];
                            while ((bytesRead = is.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                            out.close();
                            is.close();
                            part.delete();
                        } catch (IOException e) {
                            if (SynloadFramework.debug) {
                                e.printStackTrace();
                            }
                        }
                        String user = StringUtils.join(httpRequest.getBaseRequest().getParameterMap().get("user"));
                        UploadedFile uf = new UploadedFile(URLDecoder.decode(part.getSubmittedFileName(), "UTF-8"), SynloadFramework.uploadPath, tempFile, user, part.getSize());
                        EventPublisher.raiseEvent(new FileUploadEvent(uf, StringUtils.join(httpRequest.getBaseRequest().getParameterMap().get("key"))), true, null);
                        entry.add(uf.getName());
                    }
                }
            }
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            httpRequest.getResponse().getWriter().println(ow.writeValueAsString(entry));
        } catch (ServletException e) {
        	 if (SynloadFramework.debug) {
                 e.printStackTrace();
             }
        	 httpRequest.getResponse().getWriter().println("{\"e\":\"Authentication failed!\"}");
             try {
                 for (Part part : httpRequest.getRequest().getParts()) {
                     part.delete();
                 }
             } catch (ServletException e1) {
                 e1.printStackTrace();
             }
        } catch (NullPointerException e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
            httpRequest.getResponse().getWriter().println("{\"e\":\"Authentication failed!\"}");
            try {
                for (Part part : httpRequest.getRequest().getParts()) {
                    part.delete();
                }
            } catch (ServletException e1) {
                e1.printStackTrace();
            }
        }
    }
}
