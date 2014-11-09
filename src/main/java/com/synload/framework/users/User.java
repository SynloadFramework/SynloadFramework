package com.synload.framework.users;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;

@SuppressWarnings("serial")
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
public class User implements Serializable{
	public User(){}
	public User(ResultSet rs){
		try {
			this.setUsername(rs.getString("username"));
			this.flags = new ArrayList<String>(Arrays.asList(rs.getString("flags").replace("]", "").replace("[", "").split(",")));
			this.password = rs.getString("password");
			this.setEmail(rs.getString("email"));
			this.setAdmin(rs.getBoolean("admin"));
			this.setCreatedDate(rs.getLong("created_date"));
			this.setId(rs.getLong("id"));
			rs.close();
		} catch (SQLException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public User(String username, String password, String email, List<String> flags){
		this.setUsername(username.toLowerCase());
		this.setPassword(password);
		this.setEmail(email);
		this.setFlags(flags);
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("INSERT INTO `users` ( `username`, `password`, `email`, `flags`, `created_date`) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP() )");
			s.setString(1, this.getUsername());
			s.setString(2, this.getPassword());
			s.setString(3, this.getEmail());
			s.setString(4, this.getFlags().toString());
			s.execute();
			User m = User.findUser(this.getUsername());
			if(m!=null){
				this.setId(m.getId());
			}else{
				System.out.println("[ERROR] Registration error!");
			}
			//return query.setParameter("user", ).getResultList().size();
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public long id,createdDate;
	public String username = "";
	@JsonIgnore private String email = "";
	public List<String> flags = new ArrayList<String>();
	public boolean admin = false;
	@JsonIgnore private String password = "";
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<String> getFlags() {
		return flags;
	}
	public boolean hasFlag(String flag){
		return flags.contains(flag);
	}
	public void setFlags(List<String> flags) {
		this.flags = flags;
	}
	public void addFlags(String flag) {
		this.flags.add(flag);
	}
	public boolean isAdmin() {
		return admin;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("UPDATE users SET admin=? WHERE id=?");
			s.setBoolean(1, admin);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String Password) {
		String hashedPass = "";
		try {
			hashedPass = this.hashGenerator(Password);
			try{
				PreparedStatement s = SynloadFramework.sql.prepareStatement("UPDATE users SET password=? WHERE id=?");
				s.setString(1, hashedPass);
				s.setLong(2, id);
				s.execute();
				s.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		this.password = hashedPass;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean passwordMatch(String Password){
		String hashedPass = "";
		try {
			hashedPass = this.hashGenerator(Password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if(hashedPass.equals(password)){
			return true;
		}
		return false;
	}
	public static int existsUser(String user){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username FROM users WHERE username=?");
			s.setString(1, user.toLowerCase());
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				rs.close();
				s.close();
				return 1;
			}
			rs.close();
			s.close();
			return 0;
			//return query.setParameter("user", ).getResultList().size();
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
			return 0;
		}
	}
	public static List<User> all(){
		List<User> all = new ArrayList<User>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username, password, email, flags, admin, id, created_date FROM users");
			ResultSet rs = s.executeQuery();
			while(!rs.isClosed() && rs.next()){
				User u = new User(rs);
				all.add(u);
			}
			rs.close();
			s.close();
		}catch(SQLException e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		return all;
	}
	public static User findUser(String user){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username, password, email, flags, admin, id, created_date FROM users WHERE username=?");
			s.setString(1, user.toLowerCase());
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				User u = new User(rs);
				rs.close();
				s.close();
				return u;
			}
			rs.close();
			s.close();
			return null;
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
			return null;
		}
	}
	public static User findUserSession(String uuid, String ip){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT user FROM sessions WHERE session=?"); //  AND ip=?
			s.setString(1, uuid);
			//s.setString(2, ip);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				User u = findUser(rs.getLong("user"));
				rs.close();
				s.close();
				return u;
			}
			rs.close();
			s.close();
			return null;
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
			return null;
		}
	}
	public static User findVerifySession(String uuid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT user FROM sessions WHERE session=?");
			s.setString(1, uuid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				User u = findUser(rs.getLong("user"));
				rs.close();
				s.close();
				return u;
			}
			rs.close();
			s.close();
			return null;
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
			return null;
		}
	}
	public static User findUser(long uid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username, password, email, flags, admin, id, created_date FROM users WHERE id=?");
			s.setLong(1, uid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				User u = new User(rs);
				rs.close();
				s.close();
				return u;
			}
			rs.close();
			s.close();
			return null;
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
			return null;
		}
	}
	public void saveUserEmail(String email){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("UPDATE users SET email=? WHERE id=?");
			s.setString(1, email);
			s.setLong(2, id);
			s.execute();
			s.close();
			this.email = email;
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public void deleteUserSession(String ip, String uuid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("DELETE FROM sessions WHERE ip=? AND session=? AND user=?");
			s.setString(1, ip);
			s.setString(2, uuid);
			s.setLong(3, id);
			s.execute();
			s.close();
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public void saveUserSession(String ip, String uuid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("INSERT INTO sessions SET ip=?, session=?, user=?");
			s.setString(1, ip);
			s.setString(2, uuid);
			s.setLong(3, id);
			s.execute();
			s.close();
		}catch(Exception e){
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	private String hashGenerator(String Password) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(Password.getBytes());
        byte byteData[] = md.digest();
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
}