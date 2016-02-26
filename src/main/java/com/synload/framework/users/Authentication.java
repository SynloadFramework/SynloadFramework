package com.synload.framework.users;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.synload.framework.ws.WSHandler;

public class Authentication {
    public static User login(WSHandler session, String username, String password) {
        User u = null;
        if ((u = User.findUser(username)) != null) {
            if (u.passwordMatch(password)) {
                session.flags = u.getFlags();
                return u;
            }
        }
        return null;
    }
    public static User login(String username, String password) {
        User u = null;
        if ((u = User.findUser(username)) != null) {
            if (u.passwordMatch(password)) {
                return u;
            }
        }
        return null;
    }

    public static boolean create(String username, String password,
            String email, List<String> flags, int admin) {
        boolean validEmail = false;
        try {
            Pattern regex = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(email);
            validEmail = regexMatcher.matches();
        } catch (PatternSyntaxException ex) {
        }
        if (User.existsUser(username) == 0 && username.length() > 3
                && password.length() > 3 && validEmail) {
            int longTime = (int) (System.currentTimeMillis() / 1000L);
            User u;
            try {
                u = new User("username", username, "password", User.hashGenerator(password), "email",
                        email, "flags", flags.toString(), "admin", admin,
                        "created_date", longTime);
                try {
					u._insert();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public static User session(WSHandler session, String ip, String uuid) {
        User u = User.findUserSession(uuid, ip);
        if (u != null) {
            session.flags = u.getFlags();
            return u;
        }
        return null;
    }
    public static User session(String ip, String uuid) {
        User u = User.findUserSession(uuid, ip);
        if (u != null) {
            return u;
        }
        return null;
    }
}