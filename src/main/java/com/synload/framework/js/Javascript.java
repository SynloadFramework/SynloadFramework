package com.synload.framework.js;

import java.util.ArrayList;
import java.util.List;

public class Javascript {
    public List<JSCallBack> callEvents = new ArrayList<JSCallBack>();
    public String script = "";

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<JSCallBack> getCallEvents() {
        return callEvents;
    }

    public void setCallEvents(List<JSCallBack> callEvents) {
        this.callEvents = callEvents;
    }

    public void addCallBack(String function, String event) {
        JSCallBack e = new JSCallBack();
        e.setEvent(event);
        e.setFunc(function);
        this.callEvents.add(e);
    }
}