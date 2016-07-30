package com.synload.talksystem.eventShare;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.EventTrigger;
import com.synload.eventsystem.HandlerRegistry;
import com.synload.framework.http.HTTPHandler;
import com.synload.framework.http.HttpRequest;
import com.synload.framework.ws.WSHandler;
import com.synload.talksystem.Client;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Nathaniel on 7/22/2016.
 */
public class EventShare {
    public Client eventBusServer;
    public Map<String, Object> requestMap = new HashMap<String, Object>();
    public EventShare(String ip, int port, String key, boolean localShare, boolean remoteShare){
        try {
            eventBusServer = Client.createConnection(ip, port, false, key);
            eventBusServer.setEs(this);
            eventBusServer.write(new ESTypeConnection(remoteShare));
            // send Events
            if(localShare){
                this.transmitEvents();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public EventShare(Client eventBusServer){
        this.eventBusServer = eventBusServer;
    }
    public void transmitEvents(){
        // Currently transmit all events
        for(Entry<Class, List<EventTrigger>> eventGroup: HandlerRegistry.getHandlers().entrySet()){
            String annotation = eventGroup.getKey().getName();
            for(EventTrigger trigger : eventGroup.getValue()){
                try {
                    eventBusServer.write(new ESSharedEvent(annotation, trigger.getTrigger()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void transmit(EventClass e, WSHandler client){
        e.generateIdentifier();
        requestMap.put(e.getIdentifier(), client);
        try {
            eventBusServer.write(new ESPush(e));
        }catch(Exception error){
            error.printStackTrace();
        }
    }
    public void transmit(EventClass e, HttpRequest client){
        e.generateIdentifier();
        requestMap.put(e.getIdentifier(), client);
        try {
            eventBusServer.write(new ESPush(e));
        }catch(Exception error){
            error.printStackTrace();
        }
    }
    public void transmit(EventClass e, EventShare client){
        requestMap.put(e.getIdentifier(), client);
        try {
            eventBusServer.write(new ESPush(e));
        }catch(Exception error){
            error.printStackTrace();
        }
    }
    public void respond(ESData esd){
        if(requestMap.containsKey(esd.getIdentifier())){
            Object client = requestMap.get(esd.getIdentifier());
            if(WSHandler.class.isInstance(client)){
                ((WSHandler)client).send(esd.getData());
            }else if(HttpRequest.class.isInstance(client)){
                HttpRequest hr = ((HttpRequest)requestMap.get(esd.getIdentifier()));
                for(String header: esd.getHeaders()){
                    String[] headerParts = header.split(",");
                    hr.getResponse().addHeader(headerParts[0], headerParts[1]);
                }
                hr.getResponse().setStatus(HttpServletResponse.SC_OK);
                try {
                    hr.getResponse().getOutputStream().write(esd.getData().getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(EventShare.class.isInstance(client)){
                try {
                    ((EventShare) client).getEventBusServer().write(esd.getData());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            // didn't come from this server? ignore
        }
    }

    public Client getEventBusServer() {
        return eventBusServer;
    }

    public void setEventBusServer(Client eventBusServer) {
        this.eventBusServer = eventBusServer;
    }

    public Map<String, Object> getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(Map<String, Object> requestMap) {
        this.requestMap = requestMap;
    }
}
