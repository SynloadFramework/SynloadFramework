package com.synload.framework.users;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class Authentication{
	public static User login(String username, String password){
		User u = null;
		if((u = User.findUser(username)) != null){
			if(u.passwordMatch(password)){
				return u;
			}
		}
		return null;
	}
	public static boolean create(String username, String password, String email, List<String> flags, int admin){
		boolean validEmail = false;
		try {
			Pattern regex = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
			Matcher regexMatcher = regex.matcher(email);
			validEmail = regexMatcher.matches();
		} catch (PatternSyntaxException ex) {
		}
		if(User.existsUser(username)==0 && username.length()>3 && password.length()>3 && validEmail){
			new User("username", username, "password", password, "email", email, "flags", flags.toString(), "admin", admin, "created_date", "UNIX_TIMESTAMP()");
			return true;
		}else{
			return false;
		}
	}
	public static User session(String ip, String uuid){
		User u = User.findUserSession(uuid, ip);
		if(u != null){
			return u;
		}
		return null;
	}
}