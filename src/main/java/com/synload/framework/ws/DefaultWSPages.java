package com.synload.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.FullPage;
import com.synload.framework.elements.LoginBox;
import com.synload.framework.elements.RegisterBox;
import com.synload.framework.elements.Success;
import com.synload.framework.elements.UserSettingsForm;
import com.synload.framework.elements.Wrapper;
import com.synload.framework.handlers.Request;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.framework.users.Authentication;
import com.synload.framework.users.User;

public class DefaultWSPages {
	
	@Event(name="",description="",trigger={"get","full"},type=Type.WEBSOCKET)
	public void getFullPage(RequestEvent event) throws JsonProcessingException, IOException{
		event.getSession().send(
				SynloadFramework.ow.writeValueAsString(
				new FullPage(
					event.getRequest().getTemplateCache()
				)
			)
		);
	}
	
	@Event(name="",description="",trigger={"get","ping"},type=Type.WEBSOCKET)
	public void getPing(RequestEvent event) throws JsonProcessingException, IOException{
		return;
	}
	
	@Event(name="",description="",trigger={"get","wrapper"},type=Type.WEBSOCKET)
	public void getWrapper(RequestEvent event) throws JsonProcessingException, IOException{
		event.getSession().session.getRemote().sendString(
				SynloadFramework.ow.writeValueAsString(
				new Wrapper(
					event.getRequest().getTemplateCache()
				)
			)
		);
	}
	
	@Event(name="",description="",trigger={"get","userSettings"},type=Type.WEBSOCKET,flags={"r"})
	public void getUserSettingsForm(RequestEvent event) throws JsonProcessingException, IOException{
		event.getSession().send(
			SynloadFramework.ow.writeValueAsString(
				UserSettingsForm.get(
					event.getSession()
				)
			)
		);
	}
	
	@Event(name="",description="",trigger={"action","userSettings"},type=Type.WEBSOCKET,flags={"r"})
	public void getUserSettingsSave(RequestEvent event) throws JsonProcessingException, IOException{
		User mu = event.getSession().getUser();
		mu.saveUserEmail(event.getRequest().getData().get("email"));
		event.getSession().send(SynloadFramework.ow.writeValueAsString(UserSettingsForm.get(event.getSession())));
	}
	
	@Event(name="",description="",trigger={"get","login"},type=Type.WEBSOCKET)
	public void getLoginBox(RequestEvent event) throws JsonProcessingException, IOException{
		event.getSession().send(
			SynloadFramework.ow.writeValueAsString(
				new LoginBox(
					event.getRequest().getTemplateCache()
				)
			)
		);
	}
	
	@Event(name="",description="",trigger={"get","register"},type=Type.WEBSOCKET)
	public void getRegisterBox(RequestEvent event) throws JsonProcessingException, IOException{
		event.getSession().send(
			SynloadFramework.ow.writeValueAsString(
				new RegisterBox(
					event.getRequest().getTemplateCache()
				)
			)
		);
	}
	
	@Event(name="",description="",trigger={"get","sessionlogin"},type=Type.WEBSOCKET)
	public void getSessionLogin(RequestEvent event) throws JsonProcessingException, IOException{
		User authedUser = Authentication.session(
				String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP")),
				event.getRequest().getData().get("sessionid")
			);
		if(authedUser!=null){
			event.getSession().setUser(authedUser);
			Success authResponse = new Success("session");
			Map<String, String> userData = new HashMap<String,String>();
			userData.put("id", String.valueOf(event.getSession().getUser().getId()));
			userData.put("session", event.getRequest().getData().get("sessionid"));
			if(authedUser.getFlags()!=null){
				userData.put("flags", SynloadFramework.ow.writeValueAsString(event.getSession().getUser().getFlags()));
			}
			userData.put("name", event.getSession().getUser().getUsername());
			authResponse.setData(userData);
			event.getSession().send(SynloadFramework.ow.writeValueAsString(authResponse));
		}else{
			event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("session")));
		}
	}
	
	@Event(name="",description="",trigger={"get","logout"},type=Type.WEBSOCKET)
	public void getLogout(RequestEvent event) throws JsonProcessingException, IOException{
		if(event.getSession().getUser()!=null){
			event.getSession().getUser().deleteUserSession( 
				String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP")), 
				event.getRequest().getData().get("sessionid")
			);
			event.getSession().setUser(null);
			event.getSession().send(SynloadFramework.ow.writeValueAsString(new Success("logout")));
		}else{
			event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("logout")));
		}
	}
	
	@Event(name="",description="",trigger={"action","login"},type=Type.WEBSOCKET)
	public void getLogin(RequestEvent event) throws JsonProcessingException, IOException{
		User authedUser = Authentication.login(
				event.getRequest().getData().get("username").toLowerCase(),
				event.getRequest().getData().get("password"));
		if(authedUser!=null){
			String uuid = UUID.randomUUID().toString();
			event.getSession().setUser(authedUser);
			event.getSession().getUser().saveUserSession(
				String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP")),
				uuid
			);
			Success authResponse = new Success("login");
			Map<String, String> userData = new HashMap<String,String>();
			userData.put("id", String.valueOf(event.getSession().getUser().getId()));
			userData.put("session", uuid);
			if(authedUser.getFlags()!=null){
				userData.put("flags", SynloadFramework.ow.writeValueAsString(authedUser.getFlags()));
			}
			userData.put("name", event.getSession().getUser().getUsername());
			authResponse.setData(userData);
			event.getSession().send(SynloadFramework.ow.writeValueAsString(authResponse));
		}else{
			event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("login")));
		}
	}
	
	@Event(name="",description="",trigger={"action","register"},type=Type.WEBSOCKET)
	public void getRegister(RequestEvent event) throws JsonProcessingException, IOException{
		List<String> flags = new ArrayList<String>();
		flags.add("r");
		boolean authedUser = Authentication.create(
				event.getRequest().getData().get("username").toLowerCase(),
				event.getRequest().getData().get("password"),
				event.getRequest().getData().get("email"),
				flags);
		if(authedUser){
			event.getSession().send(SynloadFramework.ow.writeValueAsString(new Success("register")));
			// Should redirect to login box!
		}else{
			event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("register")));
		}
	}
	
}
