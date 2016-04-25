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
import com.synload.framework.ws.annotations.Perms;
import com.synload.framework.ws.annotations.WSEvent;

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
	
	@WSEvent(name = "Full body wrapper", description = "part of html page creation", enabled = true, method = "get", action = "full")
	@Perms({ "" })
    public void getFullPage(RequestEvent event) throws JsonProcessingException,
            IOException {
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(new FullPage(event
                        .getRequest().getTemplateCache())));
    }

	@WSEvent(name = "Wrapper", description = "part of html page creation", enabled = true, action = "wrapper", method = "get")
	@Perms({ "" })
    public void getWrapper(RequestEvent event) throws JsonProcessingException,
            IOException {
        event.getSession().session.getRemote().sendString(
                SynloadFramework.ow.writeValueAsString(new Wrapper(event
                        .getRequest().getTemplateCache())));
    }
	
	@WSEvent(name = "Encryption confirmation", description = "checks to see if data is correct", enabled = true, action = "encrypt_confirm", method = "get")
	@Perms({ "" })
    public void getEncryptAuth(RequestEvent event)
            throws JsonProcessingException, IOException {
        if (event.getSession().encrypt) {
            EventPublisher.raiseEvent(new EncryptEvent(event.getSession()),
                    null);
        }
    }

	@WSEvent(name = "Send encryption page", description = "when encryption=true sends ws page", enabled = true, action = "wrapper", method = "get")
	@Perms({ "" })
    public void getEncryptAuth(EncryptEvent event)
            throws JsonProcessingException, IOException {
        if (event.getSession().encrypt) {
            if (SynloadFramework.isSiteDefaults()) {
                event.getSession().send(new JavascriptIncludes());
            }
        }
    }
	
	@WSEvent(name = "Ping", description = "Keep alive requests", enabled = true, action = "ping", method = "get")
	@Perms({ "" })
    public void getPing(RequestEvent event) throws JsonProcessingException,
            IOException {
        return;
    }
	
    

}
