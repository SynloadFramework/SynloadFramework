package com.synload.talksystem.eventShare;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.EventTrigger;
import com.synload.eventsystem.HandlerRegistry;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.eventsystem.events.annotations.ES;
import com.synload.framework.Log;
import com.synload.framework.http.HTTPHandler;
import com.synload.framework.http.HttpRequest;
import com.synload.framework.ws.WSHandler;
import com.synload.talksystem.Client;
import net.jodah.expiringmap.ExpiringMap;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nathaniel on 7/22/2016.
 */
public class EventShare {
    public Client eventBusServer;
    public boolean localShare = false;
    public boolean remoteShare = false;
    public Map<String, Object> requestMap = ExpiringMap.builder().expiration(5, TimeUnit.SECONDS).build();
    public static List<EventShare> eventShareServers = new ArrayList<EventShare>();
    public EventShare(String ip, int port, String key, boolean localShare, boolean remoteShare){
        eventShareServers.add(this);
        try {
            eventBusServer = Client.createConnection(ip, port, false, key, true);
            eventBusServer.setEs(this);
            this.localShare = localShare;
            this.remoteShare = remoteShare;
            onConnect();
        }catch (Exception e){
            e.printStackTrace();
            Client.reconnect(this, ip, port, false, key, true);
        }
    }
    public void onConnect(){
        try{
            eventBusServer.write(new ESTypeConnection(remoteShare));
            // send Events
            if(localShare){
                transmitEvents();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public EventShare(Client eventBusServer){
        this.eventBusServer = eventBusServer;
        eventShareServers.add(this);
    }
    public void removeEvents(){
        // Currently transmit all events
        for(Entry<Class, List<EventTrigger>> eventGroup: HandlerRegistry.getHandlers().entrySet()){
            String annotation = eventGroup.getKey().getName();
            List<EventTrigger> eventTriggers = new ArrayList<EventTrigger>(eventGroup.getValue());
            for(EventTrigger trigger : eventTriggers){
                if(trigger.getServer()==this) { // do not send own events...
                    ESRemoveEvent esre = new ESRemoveEvent(annotation, trigger.getTrigger());
                    for(EventShare es : EventShare.getEventShareServers()){
                        if(es!=this) {
                            if (es.isShareOut()) {
                                try {
                                    es.getEventBusServer().write(esre);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    HandlerRegistry.getHandlers().get(eventGroup.getKey()).remove(trigger);
                    System.out.println(HandlerRegistry.getHandlers());
                }
            }
        }
    }
    public void onClose(){
        removeEvents();
    }
    public void transmitEvents(){
        // Currently transmit all events
        Log.info("Transmitting Events", EventShare.class);
        for(Entry<Class, List<EventTrigger>> eventGroup: HandlerRegistry.getHandlers().entrySet()){
            String annotation = eventGroup.getKey().getName();
            for(EventTrigger trigger : eventGroup.getValue()){
                if(trigger.getServer()!=this) { // do not send own events...
                    if(trigger.getMethod()!=null){ // its a local event
                        if(trigger.getMethod().isAnnotationPresent(ES.class)){
                            try {
                                eventBusServer.write(new ESSharedEvent(annotation, trigger.getTrigger()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        try {
                            eventBusServer.write(new ESSharedEvent(annotation, trigger.getTrigger()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public void transmit(EventClass e, WSHandler client){
        e.generateIdentifier();
        requestMap.put(e.getIdentifier(), client);
        if(RequestEvent.class.isInstance(e)){
            ((RequestEvent)e).setSession(null);
            e.setResponse(null);
        }
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
        if(RequestEvent.class.isInstance(e)){
            ((RequestEvent)e).setSession(null);
            e.setResponse(null);
        }
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
                    Log.info("Sending to another server...!", EventShare.class);
                    ((EventShare) client).getEventBusServer().write(esd);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            // didn't come from this server? ignore
            Log.info("Whoa route not found! ( "+esd.getIdentifier()+" )", EventShare.class);
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

    public boolean isShareOut() {
        return localShare;
    }

    public void setShareOut(boolean shareOut) {
        this.localShare = shareOut;
    }

    public static List<EventShare> getEventShareServers() {
        return eventShareServers;
    }

    public static void setEventShareServers(List<EventShare> eventShareServers) {
        EventShare.eventShareServers = eventShareServers;
    }
}
