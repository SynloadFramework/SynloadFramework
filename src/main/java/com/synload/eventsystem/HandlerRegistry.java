package com.synload.eventsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerRegistry {
    @SuppressWarnings("rawtypes")
	private static Map<Class, List<EventTrigger>> triggers = new HashMap<Class, List<EventTrigger>>();

    @SuppressWarnings("rawtypes")
	public static void register(Class handler, EventTrigger trigger) {
        if (triggers.containsKey(handler)) {
            if (!triggers.get(handler).contains(trigger)) {
                triggers.get(handler).add(trigger);
            } else {
                System.out.println("Trigger already registered!");
            }
        } else {
            List<EventTrigger> eventTrigger = new ArrayList<EventTrigger>();
            eventTrigger.add(trigger);
            triggers.put(handler, eventTrigger);
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