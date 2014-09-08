package com.synload.framework.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.FullPage;
import com.synload.framework.elements.LoginBox;
import com.synload.framework.elements.RegisterBox;
import com.synload.framework.elements.Success;
import com.synload.framework.elements.UserSettingsForm;
import com.synload.framework.elements.Wrapper;
import com.synload.framework.handlers.Request;
import com.synload.framework.users.Authentication;
import com.synload.framework.users.User;

public class DefaultWSPages {
	public void getFullPage(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new FullPage(request.getTemplateCache())));
	}
	public void getPing(WSHandler user, Request request) throws JsonProcessingException, IOException{
		return;
	}
	public void getWrapper(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.session.getRemote().sendString(user.ow.writeValueAsString(new Wrapper(request.getTemplateCache())));
	}
	public void getUserSettingsForm(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(UserSettingsForm.get(user)));
	}
	public void getUserSettingsSave(WSHandler user, Request request) throws JsonProcessingException, IOException{
		User mu = user.getUser();
		mu.saveUserEmail(request.getData().get("email"));
		user.send(user.ow.writeValueAsString(UserSettingsForm.get(user)));
	}
	public void getLoginBox(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new LoginBox(request.getTemplateCache())));
	}
	public void getRegisterBox(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new RegisterBox(request.getTemplateCache())));
	}
	public void getSessionLogin(WSHandler user, Request request) throws JsonProcessingException, IOException{
		User authedUser = Authentication.session(
				String.valueOf(user.session.getUpgradeRequest().getHeader("X-Real-IP")),
				request.getData().get("sessionid")
			);
		if(authedUser!=null){
			user.setUser(authedUser);
			Success authResponse = new Success("session");
			Map<String, String> userData = new HashMap<String,String>();
			userData.put("id", String.valueOf(user.getUser().getId()));
			userData.put("session", request.getData().get("sessionid"));
			if(authedUser.getFlags()!=null){
				userData.put("flags", user.ow.writeValueAsString(user.getUser().getFlags()));
			}
			userData.put("name", user.getUser().getUsername());
			authResponse.setData(userData);
			user.send(user.ow.writeValueAsString(authResponse));
		}else{
			user.send(user.ow.writeValueAsString(new Failed("session")));
		}
	}
	public void getLogout(WSHandler user, Request request) throws JsonProcessingException, IOException{
		if(user.getUser()!=null){
			user.getUser().deleteUserSession( String.valueOf(user.session.getUpgradeRequest().getHeaders("X-Real-IP")), request.getData().get("sessionid"));
			user.setUser(null);
			user.send(user.ow.writeValueAsString(new Success("logout")));
		}else{
			user.send(user.ow.writeValueAsString(new Failed("logout")));
		}
	}
	public void getLogin(WSHandler user, Request request) throws JsonProcessingException, IOException{
		User authedUser = Authentication.login(
				request.getData().get("username").toLowerCase(),
				request.getData().get("password"));
		if(authedUser!=null){
			String uuid = UUID.randomUUID().toString();
			user.setUser(authedUser);
			user.getUser().saveUserSession(String.valueOf(user.session.getUpgradeRequest().getHeaders("X-Real-IP")), uuid);
			Success authResponse = new Success("login");
			Map<String, String> userData = new HashMap<String,String>();
			userData.put("id", String.valueOf(user.getUser().getId()));
			userData.put("session", uuid);
			if(authedUser.getFlags()!=null){
				userData.put("flags", user.ow.writeValueAsString(authedUser.getFlags()));
			}
			userData.put("name", user.getUser().getUsername());
			authResponse.setData(userData);
			user.send(user.ow.writeValueAsString(authResponse));
		}else{
			user.send(user.ow.writeValueAsString(new Failed("login")));
		}
	}
	public void getRegister(WSHandler user, Request request) throws JsonProcessingException, IOException{
		List<String> flags = new ArrayList<String>();
		flags.add("r");
		boolean authedUser = Authentication.create(
				request.getData().get("username").toLowerCase(),
				request.getData().get("password"),
				request.getData().get("email"),
				flags);
		if(authedUser){
			user.send(user.ow.writeValueAsString(new Success("register")));
			// Should redirect to login box!
		}else{
			user.send(user.ow.writeValueAsString(new Failed("register")));
		}
	}
	
}
