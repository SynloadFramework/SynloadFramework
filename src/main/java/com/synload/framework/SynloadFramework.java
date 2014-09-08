package com.synload.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.Addon;
import com.synload.framework.handlers.Request;
import com.synload.framework.http.DefaultHTTPPages;
import com.synload.framework.http.HTTPHandler;
import com.synload.framework.http.HTTPResponse;
import com.synload.framework.http.HTTPRouting;
import com.synload.framework.js.Javascript;
import com.synload.framework.menu.MenuItem;
import com.synload.framework.ws.DefaultWSPages;
import com.synload.framework.ws.WSHandler;
import com.synload.framework.ws.WSRequest;
import com.synload.framework.ws.WSResponse;
import com.synload.framework.ws.WSRouting;
import com.synload.framework.ws.WebsocketHandler;

public class SynloadFramework{
	public SynloadFramework(){}
	public static HashMap<String,HashMap<String,Object>> htmlFiles = new HashMap<String,HashMap<String,Object>>();
	public static List<Session> users = new ArrayList<Session>();
	//public static Map<String,DashboardGroup> dashboardGroups = new HashMap<String,DashboardGroup>();
	public static List<Object> plugins = new ArrayList<Object>();
	public static List<String> bannedIPs = new ArrayList<String>();
	public static Connection sql = null;
	public static int totalFailures = 10;
	public static boolean debug = false;
	public static Server server = null;
	public static Properties prop = new Properties();
	public static List<WSHandler> clients = new ArrayList<WSHandler>();
	public static Map<String,List<Long>> failedAttempts = new HashMap<String,List<Long>>();
	public static List<Javascript> javascripts = new ArrayList<Javascript>();
	public static String authKey = "s9V0l3v1GsrE2j50VrUp1Elp1jY4Xh97bNkuHBnOVCL28I"+
			"TyH17u5TRD25UDsRrb2Bny61y1XXv0zZSWq4O9gARzO881amS3lAgy";
	public static List<MenuItem> menus = new ArrayList<MenuItem>();
	public static HashMap<String,MenuItem> referenceMenus = new HashMap<String,MenuItem>();
	public static void main(String[] args) {
		try {
			System.out.println("\t"+"Starting Synload Development Framework Server");
			if((new File("config.ini")).exists()){
				prop.load(new FileInputStream("config.ini"));
				authKey = prop.getProperty("authKey");
			}else{
				prop.setProperty("authKey", authKey);
				prop.setProperty("jdbc", "jdbc:mysql://localhost:3306/db");
				prop.setProperty("dbuser", "root");
				prop.setProperty("dbpass", "");
				prop.setProperty("debug", "false");
				prop.store(new FileOutputStream("config.ini"), null);
			}
			
			debug = Boolean.valueOf(prop.getProperty("debug"));
			
			System.out.println("[DS] Loading defaults");
			SynloadFramework.buildMenu();
			SynloadFramework.buildDefaultPages();
			SynloadFramework.buildDefaultHTTP();
			SynloadFramework.buildJavascript();
			System.out.println("[DS] Fully loaded defaults");
			System.out.println("[ML] Loading modules");
	        JarClassLoader jcl = new JarClassLoader();
	        String path = "modules/";
	        String fileName;
	        File folder = new File(path);
	        if(!folder.exists()){
	            folder.mkdir();
	        }
	        
	        sql = DriverManager.getConnection( 
					prop.getProperty("jdbc"), 
					prop.getProperty("dbuser"), 
					prop.getProperty("dbpass") );
	        
	        File[] listOfFiles = folder.listFiles(); 
	        for (int i = 0; i < listOfFiles.length; i++){
	            if (listOfFiles[i].isFile()){
	                fileName = listOfFiles[i].getName();
	                if (fileName.endsWith(".jar")){
	                    jcl = new JarClassLoader();
	                    String[] filedata = fileName.split("\\.");
	                    jcl.add("modules/"+fileName);
	                    JclObjectFactory factory = JclObjectFactory.getInstance();
	                    InputStream is = jcl.getResourceAsStream("config.ini");
	                    Properties mProperties = new Properties();
	                    mProperties.load(is);
	                    Object obj = factory.create(jcl, mProperties.getProperty("class"));
	                    plugins.add(obj);
	                    ((Addon)obj).init();
	                    System.out.println("[ML] Loaded module "+filedata[0]);
	                }
	            }
	        }
	        System.out.println("[ML] Modules fully loaded");
			int port = 80;
			if(args.length>=1){
				port = Integer.valueOf(args[0]);
			}
			
			server = new Server(port);
			HandlerCollection handlerCollection = new HandlerCollection();
			handlerCollection.addHandler(new HTTPHandler());
			handlerCollection.addHandler(new WebsocketHandler());
			server.setHandler(handlerCollection);
			System.out.println("[SR] Started server on port "+port);
			server.start();
			server.join();
			
		} catch (Exception e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	
	public static void buildJavascript(){
		Javascript jsUser = new Javascript();
		jsUser.addCallBack("user.msg", "recieve");
		jsUser.addCallBack("user.load", "init");
		jsUser.addCallBack("user.event", "login");
		jsUser.addCallBack("user.event", "logout");
		jsUser.addCallBack("user.event", "session");
		jsUser.addCallBack("user.event", "register");
		jsUser.setScript("/js/user.js");
		SynloadFramework.registerJavascriptFile(jsUser,"User Account System");
		Javascript jsPage = new Javascript();
		jsPage.setScript("/js/page.js");
		SynloadFramework.registerJavascriptFile(jsPage,"Page Handler");
		Javascript jsForm = new Javascript();
		jsForm.setScript("/js/form.js");
		SynloadFramework.registerJavascriptFile(jsForm,"Form Handler");
	}
	
	public static void buildDefaultHTTP(){
		SynloadFramework.registerHTTPPage("/", DefaultHTTPPages.class, "getIndex");
	}
	
	public static void buildDefaultPages(){
		List<String> flagsRegistered = new ArrayList<String>();
		flagsRegistered.add("r");
		SynloadFramework.registerElement(new WSRequest("full","get"), DefaultWSPages.class, "getFullPage", new ArrayList<String>());
		/* USER ACCOUNT DATA */
		SynloadFramework.registerElement(new WSRequest("wrapper","get"), DefaultWSPages.class, "getWrapper", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("userSettings","get"), DefaultWSPages.class, "getUserSettingsForm", flagsRegistered);
		SynloadFramework.registerElement(new WSRequest("userSettings","action"), DefaultWSPages.class, "getUserSettingsSave", flagsRegistered);
		SynloadFramework.registerElement(new WSRequest("login","get"), DefaultWSPages.class, "getLoginBox", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("logout","get"), DefaultWSPages.class, "getLogout", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("register","get"), DefaultWSPages.class, "getRegisterBox", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("sessionlogin","get"), DefaultWSPages.class, "getSessionLogin", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("login","action"), DefaultWSPages.class, "getLogin", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("register","action"), DefaultWSPages.class, "getRegister", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("ping","get"), DefaultWSPages.class, "getPing", new ArrayList<String>());
	}
	
	public static void buildMenu(){
		MenuItem menu = SynloadFramework.createMenu("Account", "account", new Request("", ""), null, "r", 999);
		SynloadFramework.createMenu("Edit Profile", "usersettings", new Request("get", "userSettings"), menu, "r", 7);
		SynloadFramework.registerMenuItem(menu);
		
	}
	
	public static void registerMenuItem(MenuItem mi){
		SynloadFramework.menus.add(mi);
	}
	
	public static MenuItem createMenu(String name, String uname, Request request, MenuItem parent, String flag, Integer priority){
		System.out.println("[MU] Added "+name+" with parent <"+((parent!=null)?parent.getName():"")+">");
		MenuItem m = new MenuItem();
		m.setName(name);
		m.setUname(uname);
		m.setFlag(flag);
		m.setPriority(priority);
		m.setRequest(request);
		if(parent!=null){
			parent.addMenus(m);
		}
		return m;
	}
	public static void broadcast(String data){
		for(WSHandler user: SynloadFramework.clients){
			user.send(data);
		}
	}
	public static void broadcast(List<WSHandler> _cs, String data){
		for(WSHandler user: _cs){
			user.send(data);
		}
	}
	public static void log(String data){
		System.out.print(data);
	}
	public static void registerJavascriptFile(Javascript js, String name){
		System.out.println("[JS] Registered javascript <"+name+">");
		SynloadFramework.javascripts.add(js);
	}
	
	public static void registerHTTPPage(String page, Class<?> listenerClass, String method){
		try {
			if(HTTPRouting.addRoutes(page, new HTTPResponse(listenerClass, method))){
				System.out.println("[WB] Registered path <"+page+">");
			}else{
				System.out.println("[WB][E] Failed to add <"+page+"> path");
			}
		} catch (Exception e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	
	public static void registerElement(WSRequest page, Class<?> listenerClass, String method, List<String> flags){
		try {
			if(WSRouting.addRoutes(page, new WSResponse(listenerClass, method, flags))){
				System.out.println("[PG] Registered page <"+page.getPri()+"> with action <"+page.getRequest()+">");
			}else{
				System.out.println("[PG][E] Failed to add <"+page.getPri()+"> with action <"+page.getRequest()+">");
			}
		} catch (JsonProcessingException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	
}