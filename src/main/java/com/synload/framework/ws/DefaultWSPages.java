package com.synload.framework.ws;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.EncryptEvent;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.FullPage;
import com.synload.framework.elements.JavascriptIncludes;
import com.synload.framework.elements.Wrapper;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;

public class DefaultWSPages {

    /*
    @Event(name = "", description = "", trigger = { "get", "userSettings" }, type = Type.WEBSOCKET, flags = { "r" })
    public void getUserSettingsForm(RequestEvent event)
            throws JsonProcessingException, IOException {
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(UserSettingsForm
                        .get(event.getSession())));
    }

    @Event(name = "", description = "", trigger = { "action", "userSettings" }, type = Type.WEBSOCKET, flags = { "r" })
    public void getUserSettingsSave(RequestEvent event)
            throws JsonProcessingException, IOException {
        User mu = event.getSession().getUser();
        mu.saveUserEmail(event.getRequest().getData().get("email"));
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(UserSettingsForm
                        .get(event.getSession())));
    }
     */
	
	@Event(name = "Full body wrapper", description = "part of html page creation", trigger = { "get", "full" }, type = Type.WEBSOCKET)
    public void getFullPage(RequestEvent event) throws JsonProcessingException,
            IOException {
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(new FullPage(event
                        .getRequest().getTemplateCache())));
    }

    @Event(name = "Wrapper", description = "part of html page creation", trigger = { "get", "wrapper" }, type = Type.WEBSOCKET)
    public void getWrapper(RequestEvent event) throws JsonProcessingException,
            IOException {
        event.getSession().session.getRemote().sendString(
                SynloadFramework.ow.writeValueAsString(new Wrapper(event
                        .getRequest().getTemplateCache())));
    }
	
	@Event(name = "Encryption confirmation", description = "checks to see if data is correct", trigger = { "get", "encrypt_confirm" }, type = Type.WEBSOCKET)
    public void getEncryptAuth(RequestEvent event)
            throws JsonProcessingException, IOException {
        if (event.getSession().encrypt) {
            EventPublisher.raiseEvent(new EncryptEvent(event.getSession()),
                    null);
        }
    }

    @Event(name = "Send encryption page", description = "when encryption=true sends ws page", trigger = {}, type = Type.WEBSOCKET)
    public void getEncryptAuth(EncryptEvent event)
            throws JsonProcessingException, IOException {
        if (event.getSession().encrypt) {
            if (SynloadFramework.isSiteDefaults()) {
                event.getSession().send(new JavascriptIncludes());
            }
        }
    }
	
	@Event(name = "Ping", description = "Keep alive requests", trigger = { "get", "ping" }, type = Type.WEBSOCKET)
    public void getPing(RequestEvent event) throws JsonProcessingException,
            IOException {
        return;
    }
	
    

}
