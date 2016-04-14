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
import com.synload.framework.modules.annotations.sql.BigIntegerColumn;
import com.synload.framework.modules.annotations.sql.BooleanColumn;
import com.synload.framework.modules.annotations.sql.LongBlobColumn;
import com.synload.framework.modules.annotations.sql.SQLTable;
import com.synload.framework.modules.annotations.sql.StringColumn;
import com.synload.framework.sql.Model;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@SQLTable(name = "User Model", version = 1.3, description = "users table contains passwords and emails")
public class User extends Model {

    @BigIntegerColumn(length = 20, Key = true, AutoIncrement = true)
    public long id;

    @BigIntegerColumn(length = 11)
    public long created_date;

    @StringColumn(length = 128)
    public String username;

    @JsonIgnore
    @StringColumn(length = 128)
    public String email;

    @LongBlobColumn()
    public String flags;

    @BooleanColumn()
    public boolean admin;

    @JsonIgnore
    @StringColumn(length = 255)
    public String password;

    // public User(){}
    /*
     * public User(ResultSet rs){ try {
     * this.setUsername(rs.getString("username")); this.flags =
     * rs.getString("flags"); this.password = rs.getString("password");
     * this.setEmail(rs.getString("email"));
     * this.setAdmin(rs.getBoolean("admin"));
     * this.setCreatedDate(rs.getLong("created_date"));
     * this.setId(rs.getLong("id")); rs.close(); } catch (SQLException e) {
     * if(SynloadFramework.debug){ e.printStackTrace(); } } }
     */
    public User(ResultSet rs) {
        super(rs);
    }

    public User(Object... data) {
        super(data);
    }

    /*
     * public User(String username, String password, String email, List<String>
     * flags, int admin){ this.setUsername(username.toLowerCase());
     * this.setPassword(password); this.setEmail(email); this.setFlags(flags);
     * try{ PreparedStatement s = SynloadFramework.sql.prepareStatement(
     * "INSERT INTO `users` ( `username`, `password`, `email`, `flags`, `created_date`,`admin`) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP(), ? )"
     * ); s.setString(1, this.getUsername()); s.setString(2,
     * this.getPassword()); s.setString(3, this.getEmail()); s.setString(4,
     * this.getFlags().toString()); s.setInt(5, admin); s.execute(); s.close();
     * User m = User.findUser(this.getUsername()); if(m!=null){
     * this.setId(m.getId()); }else{
     * System.out.println("[ERROR] Registration error!"); } //return
     * query.setParameter("user", ).getResultList().size(); }catch(Exception e){
     * if(SynloadFramework.debug){ e.printStackTrace(); } } }
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getFlags() {
        return new ArrayList<String>(Arrays.asList(flags.replace("]", "")
                .replace("[", "").split(",")));
    }

    public boolean hasFlag(String flag) {
        ArrayList<String> flagsV = new ArrayList<String>(Arrays.asList(flags
                .replace("]", "").replace("[", "").split(",")));
        return flagsV.contains(flag);
    }

    public void setFlags(List<String> flags) {
        this.flags = flags.toString();
    }

    public void addFlags(String flag) {
        ArrayList<String> flagsV = new ArrayList<String>(Arrays.asList(flags
                .replace("]", "").replace("[", "").split(",")));
        flagsV.add(flag);
        flags = flagsV.toString();
    }

    public boolean isAdmin() {
        return admin;
    }

    public long getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(long createdDate) {
        this.created_date = createdDate;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("UPDATE users SET admin=? WHERE id=?");
            s.setBoolean(1, admin);
            s.setLong(2, id);
            s.execute();
            s.close();
        } catch (Exception e) {
            if (SynloadFramework.debug) {
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
            hashedPass = User.hashGenerator(Password);
            try {
                PreparedStatement s = SynloadFramework.sql
                        .prepareStatement("UPDATE users SET password=? WHERE id=?");
                s.setString(1, hashedPass);
                s.setLong(2, id);
                s.execute();
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            if (SynloadFramework.debug) {
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

    public boolean passwordMatch(String Password) {
        String hashedPass = "";
        try {
            hashedPass = User.hashGenerator(Password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (hashedPass.equals(password)) {
            return true;
        }
        return false;
    }

    public static int existsUser(String user) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("SELECT username FROM users WHERE username=?");
            s.setString(1, user.toLowerCase());
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                rs.close();
                s.close();
                return 1;
            }
            rs.close();
            s.close();
            return 0;
            // return query.setParameter("user", ).getResultList().size();
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static List<User> all() {
        List<User> all = new ArrayList<User>();
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("SELECT username, password, email, flags, admin, id, created_date FROM users");
            ResultSet rs = s.executeQuery();
            while (!rs.isClosed() && rs.next()) {
                User u = new User(rs);
                all.add(u);
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
        return all;
    }

    public static User findUser(String user) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("SELECT username, password, email, flags, admin, id, created_date FROM users WHERE username=?");
            s.setString(1, user.toLowerCase());
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                User u = new User(rs);
                rs.close();
                s.close();
                return u;
            }
            rs.close();
            s.close();
            return null;
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static User findUserSession(String uuid, String ip) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("SELECT user FROM sessions WHERE session=?"); // AND
                                                                                    // ip=?
            s.setString(1, uuid);
            // s.setString(2, ip);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                User u = findUser(rs.getLong("user"));
                rs.close();
                s.close();
                return u;
            }
            rs.close();
            s.close();
            return null;
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static User findVerifySession(String uuid) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("SELECT user FROM sessions WHERE session=?");
            s.setString(1, uuid);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                User u = findUser(rs.getLong("user"));
                rs.close();
                s.close();
                return u;
            }
            rs.close();
            s.close();
            return null;
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static User findUser(long uid) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("SELECT username, password, email, flags, admin, id, created_date FROM users WHERE id=?");
            s.setLong(1, uid);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                User u = new User(rs);
                rs.close();
                s.close();
                return u;
            }
            rs.close();
            s.close();
            return null;
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void saveUserEmail(String email) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("UPDATE users SET email=? WHERE id=?");
            s.setString(1, email);
            s.setLong(2, id);
            s.execute();
            s.close();
            this.email = email;
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUserSession(String ip, String uuid) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("DELETE FROM sessions WHERE ip=? AND session=? AND user=?");
            s.setString(1, ip);
            s.setString(2, uuid);
            s.setLong(3, id);
            s.execute();
            s.close();
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
    }

    public void saveUserSession(String ip, String uuid) {
        try {
            PreparedStatement s = SynloadFramework.sql
                    .prepareStatement("INSERT INTO sessions SET ip=?, session=?, user=?");
            s.setString(1, ip);
            s.setString(2, uuid);
            s.setLong(3, id);
            s.execute();
            s.close();
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
    }

    public static String hashGenerator(String Password)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(Password.getBytes());
        byte byteData[] = md.digest();
        // convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }
}