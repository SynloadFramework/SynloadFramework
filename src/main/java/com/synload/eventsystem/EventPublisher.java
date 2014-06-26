package com.synload.eventsystem;

import java.lang.reflect.Method;

import com.synload.framework.SynloadFramework;

public class EventPublisher {
	public static void raiseEventThread(final Event event, boolean threaded) {
		if(threaded){
			new Thread() {
	            @Override
	            public void run() {
	                raise(event);
	            }
	        }.start();
		}else{
			raise(event);
		}
	}
    public static void raiseEventThread(final Event event) {
        new Thread() {
            @Override
            public void run() {
                raise(event);
            }
        }.start();
    }
    public static void raiseEvent(final Event event) {
        raise(event);
    }
    
    @SuppressWarnings("rawtypes")
    private static void raise(final Event event) {
        for (Class handler : HandlerRegistry.getHandlers()) {
            Method[] methods = handler.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                EventHandler eventHandler = methods[i].getAnnotation(EventHandler.class);
                if (eventHandler != null) {
                    Class[] methodParams = methods[i].getParameterTypes();

                    if (methodParams.length < 1)
                        continue;

                    if (!event.getClass().getSimpleName()
                            .equals(methodParams[0].getSimpleName()))
                        continue;

                    try {
                        methods[i].invoke(handler.newInstance(), event);
                    } catch (Exception e) {
                    	if(SynloadFramework.debug){
                    		e.printStackTrace();
                    	}
                    }
                }
            }
        }
    }
}