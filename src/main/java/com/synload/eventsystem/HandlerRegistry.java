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
        triggers.compute(handler, (key, existing) -> {
            if (existing == null) {
                List<EventTrigger> eventTrigger = new CopyOnWriteArrayList<EventTrigger>();
                eventTrigger.add(trigger);
                return eventTrigger;
            } else {
                if (!existing.contains(trigger)) {
                    existing.add(trigger);
                } else {
                    System.out.println("Trigger already registered!");
                }
                return existing;
            }
        });
    }

    public static void unregister(Class handler, EventTrigger trigger) {
        if (triggers.containsKey(handler)) {
            triggers.get(handler).remove(trigger);
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