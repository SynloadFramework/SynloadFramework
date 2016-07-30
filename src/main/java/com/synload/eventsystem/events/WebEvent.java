package com.synload.eventsystem.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;

public class WebEvent extends EventClass {
    public String target;
    public Request baseRequest;
    public HttpServletRequest request;
    public HttpServletResponse response;
    public String[] URI;

    public WebEvent(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response,
            String[] URI) {
        this.setBaseRequest(baseRequest);
        this.setRequest(request);
        this.setHandler(Handler.EVENT);
        this.setType(Type.HTTP);
        this.setTarget(target);
        this.setResponse(response);
        this.setURI(URI);
        this.setTrigger(new String[] {});
    }

    public String[] getURI() {
        return URI;
    }

    public void setURI(String[] URI) {
        this.URI = URI;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Request getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(Request baseRequest) {
        this.baseRequest = baseRequest;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getHTTPResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
