package com.synload.framework.users;

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

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
public class User{
	public User(ResultSet rs){
		try {
			this.setUsername(rs.getString("username"));
			this.flags = new ArrayList<String>(Arrays.asList(rs.getString("flags").replace("]", "").replace("[", "").split(",")));
			this.password = rs.getString("password");
			this.setEmail(rs.getString("email"));
			this.setAdmin(rs.getBoolean("admin"));
			this.setId(rs.getLong("id"));
			this.ip = rs.getString("ip");
			this.session = rs.getString("session");
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public User(String username, String password, String email, List<String> flags){
		this.setUsername(username.toLowerCase());
		this.setPassword(password);
		this.setEmail(email);
		this.setFlags(flags);
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("INSERT INTO `users` ( `username`, `password`, `email`, `flags`) VALUES (?, ?, ?, ?)");
			s.setString(1, this.getUsername());
			s.setString(2, this.getPassword());
			s.setString(3, this.getEmail());
			s.setString(4, this.getFlags().toString());
			s.execute();
			User m = User.findUser(this.getUsername());
			if(m!=null){
				this.setId(m.get_id());
			}else{
				System.out.println("[ERROR] Registration error!");
			}
			//return query.setParameter("user", ).getResultList().size();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private long id;
	private String username, email, ip, session = "";
	private List<String> flags = new ArrayList<String>();
	private boolean admin = false;
	@JsonIgnore private String password = "";
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSessionID() {
		return session;
	}
	public void setSessionID(String session) {
		this.session = session;
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
	public long get_id() {
		return id;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
			e.printStackTrace();
			return 0;
		}
	}
	public static User findUser(String user){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username, password, email, flags, admin, id, ip, session FROM users WHERE username=?");
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
			e.printStackTrace();
			return null;
		}
	}
	public static User findUserSession(String uuid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username, password, email, flags, admin, id, ip, session FROM users WHERE session=?");
			s.setString(1, uuid);
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
			return null;
		}
	}
	public static User findUser(long uid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("SELECT username, password, email, flags, admin, id, ip, session FROM users WHERE id=?");
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
	public void saveUserSession(String ip, String uuid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement("UPDATE users SET ip=?, session=? WHERE id=?");
			s.setString(1, ip);
			s.setString(2, uuid);
			s.setLong(3, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
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