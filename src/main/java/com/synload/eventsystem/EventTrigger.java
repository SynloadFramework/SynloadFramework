package com.synload.eventsystem;

import java.lang.reflect.Method;

import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Event.Type;


public class EventTrigger {
    private Class hostClass;
    private Method method;
    private Object route;
    private String[] trigger;
    private String[] flags;
    private Class type;
    private Type eventType;
    private ModuleClass module;

    public Class getHostClass() {
        return hostClass;
    }

    public void setHostClass(Class hostClass) {
        this.hostClass = hostClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ModuleClass getModule() {
        return module;
    }

    public void setModule(ModuleClass module) {
        this.module = module;
    }

    public Object getRoute() {
        return route;
    }

    public void setRoute(Object rte) {
        this.route = rte;
    }

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public String[] getTrigger() {
		return trigger;
	}

	public void setTrigger(String[] trigger) {
		this.trigger = trigger;
	}

	public String[] getFlags() {
		return flags;
	}

	public void setFlags(String[] flags) {
		this.flags = flags;
	}

	public Type getEventType() {
		return eventType;
	}

	public void setEventType(Type eventType) {
		this.eventType = eventType;
	}
}
