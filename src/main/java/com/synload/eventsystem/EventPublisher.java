package com.synload.eventsystem;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.eventsystem.events.WebEvent;
import com.synload.framework.Log;
import com.synload.framework.ws.WSHandler;
import com.synload.talksystem.eventShare.ESHandler;

public class EventPublisher {
    public static void raiseEvent(final EventClass event, boolean threaded,
            final String target) {
        if (threaded) {
            new Thread() {
                @Override
                public void run() {
                    raise(event, target);
                }
            }.start();
        } else {
            raise(event, target);
        }
    }

    public static boolean raiseEvent(final EventClass event, String target) {
        return raise(event, target);
    }

    private static boolean raise(final EventClass event, String target) {
        //System.out.println("event class "+event.getClass().getName());
        //System.out.println("check "+event.getHandler().getAnnotationClass().getName());
        boolean eventCalled = false;
        if (HandlerRegistry.getHandlers().containsKey(event.getHandler().getAnnotationClass())) {
            //System.out.println("found "+event.getHandler().getAnnotationClass().getName());
            for (EventTrigger trigger : HandlerRegistry.getHandlers(event.getHandler().getAnnotationClass())){
                //System.out.println("check trigger "+trigger.getTrigger());
                if (event instanceof RequestEvent) {
                    // Websocket Event!
                    //System.out.println("Comparing");
                    if (trigger.getTrigger().length == 2) {
                        RequestEvent requestEvent = (RequestEvent) event;
                        //System.out.println(trigger.getTrigger()[0]+" to method "+requestEvent.getRequest().getMethod());
                        //System.out.println(trigger.getTrigger()[1]+" to action "+requestEvent.getRequest().getAction());
                        if (
                            trigger.getTrigger()[0].equalsIgnoreCase(requestEvent.getRequest().getMethod()) // method / method
                            && trigger.getTrigger()[1].equalsIgnoreCase(requestEvent.getRequest().getAction()) // action / action
                        ) {
                            try {
                                if(trigger.getServer()==null) {
                                    //Log.info("Event processed ", EventPublisher.class);
                                    trigger.getMethod().invoke(trigger.getHostClass().newInstance(), requestEvent);
                                }else if(event.getIdentifier()!=null){
                                    Log.info("Event transmitted [ESHANDLER]", EventPublisher.class);
                                    trigger.getServer().transmit(event, ((ESHandler)event.getResponse()).getEs());
                                }else{
                                    Log.info("Event transmitted [WSHandler]", EventPublisher.class);
                                    trigger.getServer().transmit(event, ((RequestEvent) event).getSession());
                                }
                                eventCalled = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }
                } else { // No more WebEvent, handled in HttpRouting, Custom Events Only
                    if (trigger.getMethod().getParameterTypes().length > 0 && trigger.getMethod().getParameterTypes()[0].isInstance(event)) {
                        try {
                            trigger.getMethod().invoke(trigger.getHostClass().newInstance(), event); // No EventShare yet
                            eventCalled = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return eventCalled;
    }
}