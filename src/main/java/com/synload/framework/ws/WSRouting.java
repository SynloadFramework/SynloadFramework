package com.synload.framework.ws;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.handlers.Request;

public class WSRouting {
    public static HashMap<String, WSResponse> routes = new HashMap<String, WSResponse>();

    public static HashMap<String, WSResponse> getRoutes() {
        return routes;
    }

    /*public static boolean addRoutes(WSRequest p, WSResponse h)
            throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer()
                .withDefaultPrettyPrinter();
        if (WSRouting.routes.containsKey(ow.writeValueAsString(p))) {
            return false;
        } else {
            WSRouting.routes.put(ow.writeValueAsString(p), h);
            if (WSRouting.routes.containsKey(ow
                    .writeValueAsString(new WSRequest(p.getPri(), p
                            .getRequest())))) {
                return true;
            } else {
                return false;
            }
        }
    }*/

    public static void page(WSHandler ws, Request request)throws JsonProcessingException {
        RequestEvent re = new RequestEvent(ws, request);
        re.setSessionData(ws.getSessionData());
        EventPublisher.raiseEvent(re, null); // multithreaded
        return;
        //ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        //WSRequest pg = new WSRequest(request.getMethod(), request.getAction());
        // System.out.println("[WR][I] Request recieved!");
        /*if (routes.containsKey(ow.writeValueAsString(pg))) {
            // System.out.println("[WR][I] Route Found!");
            WSResponse p = routes.get(ow.writeValueAsString(pg));
            boolean flagRequirementsMet = true;
            if (p.getRequiredFlags().size()>0) {
                for (String flag : p.getRequiredFlags()) {
                    if (!ws.getFlags().contains(flag)) {
                        flagRequirementsMet = false;
                    }
                }
            } else if (p.getRequiredFlags().size() == 0) {
                flagRequirementsMet = true;
            } else {
                flagRequirementsMet = false;
            }
            if (flagRequirementsMet) {
                try {
                    // System.out.println("[WR][I] Route found sending to method!");
                    p.getListener()
                            .getMethod(p.getMethod(), WSHandler.class,
                                    Request.class)
                            .invoke(p.getListener().newInstance(), ws,
                                    request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Flag does not exist
                // System.out.println("[WR][E] Flag not found with user!");
                return;
            }
        } else {*/
            // route does not exist send out to more complex modules
            // System.out.println("[WR][W] Route does not exist!");

        //}
    }
}