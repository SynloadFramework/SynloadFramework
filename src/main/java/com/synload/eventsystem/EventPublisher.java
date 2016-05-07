package com.synload.eventsystem;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.eventsystem.events.WebEvent;

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

    public static void raiseEvent(final EventClass event, String target) {
        raise(event, target);
    }

    private static void raise(final EventClass event, String target) {
        if (HandlerRegistry.getHandlers().containsKey(event.getHandler().getAnnotationClass())) {
            for (EventTrigger trigger : HandlerRegistry.getHandlers(event.getHandler().getAnnotationClass())){
                if (event.getClass() == RequestEvent.class) {
                    // Websocket Event!
                    System.out.println("Comparing");
                    if (trigger.getTrigger().length == 2) {
                        RequestEvent requestEvent = (RequestEvent) event;
                        System.out.println(trigger.getTrigger()[0]+" to method "+requestEvent.getRequest().getMethod());
                        System.out.println(trigger.getTrigger()[1]+" to action "+requestEvent.getRequest().getAction());
                        if (
                                trigger.getTrigger()[0].equalsIgnoreCase(requestEvent.getRequest().getMethod()) // method / method
                                && trigger.getTrigger()[1].equalsIgnoreCase(requestEvent.getRequest().getAction()) // action / action
                        ) {
                            try {
                                trigger.getMethod().invoke(trigger.getHostClass().newInstance(), requestEvent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    // Don't raise event if trigger does not exist.
                    /*else {
                        if (trigger.getMethod().getParameterTypes()[0].isInstance(event)) {
                            try {
                                trigger.getMethod().invoke(trigger.getHostClass().newInstance(), event);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }*/

                } else if (event.getClass() != RequestEvent.class) { // No more WebEvent, handled in HttpRouting, Custom Events Only
                    if (trigger.getMethod().getParameterTypes()[0].isInstance(event)) {
                        try {
                            trigger.getMethod().invoke(trigger.getHostClass().newInstance(), event);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}