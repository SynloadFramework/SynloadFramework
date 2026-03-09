package com.synload.eventsystem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class HandlerRegistry {
    @SuppressWarnings("rawtypes")
    private static Map<Class, List<EventTrigger>> triggers = new ConcurrentHashMap<Class, List<EventTrigger>>();

    @SuppressWarnings("rawtypes")
    public static void register(Class handler, EventTrigger trigger) {
        //System.out.println(handler.getName() + " trigger "+trigger.getTrigger() );
        if (triggers.containsKey(handler)) {
            if (!triggers.get(handler).contains(trigger)) {
                triggers.get(handler).add(trigger);
            } else {
                System.out.println("Trigger already registered!");
            }
        } else {
            List<EventTrigger> eventTrigger = new CopyOnWriteArrayList<EventTrigger>();
            eventTrigger.add(trigger);
            triggers.put(handler, eventTrigger);
        }
    }
    
    public static void unregister(Class handler, EventTrigger trigger) {
        if (triggers.containsKey(handler)) {
            if (triggers.get(handler).contains(trigger)) {
                triggers.get(handler).remove(trigger);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static Map<Class, List<EventTrigger>> getHandlers() {
        return triggers;
    }

    @SuppressWarnings("rawtypes")
    public static List<EventTrigger> getHandlers(Class handler) {
        return triggers.get(handler);
    }
}