package com.synload.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.spdy.server.http.HTTPSPDYServerConnector;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.framework.http.DefaultHTTPPages;
import com.synload.framework.http.HTTPHandler;
import com.synload.framework.http.HTTPResponse;
import com.synload.framework.http.HTTPRouting;
import com.synload.framework.js.Javascript;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleRegistry;
import com.synload.framework.sql.SQLRegistry;
import com.synload.framework.ws.WSHandler;
import com.synload.framework.ws.WSRequest;
import com.synload.framework.ws.WSResponse;
import com.synload.framework.ws.WSRouting;
import com.synload.framework.ws.WebsocketHandler;
import com.synload.talksystem.ServerTalk;

public class SynloadFramework {
    public SynloadFramework() {
    }

    public static HashMap<String, HashMap<String, Object>> htmlFiles = new HashMap<String, HashMap<String, Object>>();
    public static List<Session> users = new ArrayList<Session>();
    // public static Map<String,DashboardGroup> dashboardGroups = new
    // HashMap<String,DashboardGroup>();
    public static List<ModuleClass> plugins = new ArrayList<ModuleClass>();
    public static List<String> bannedIPs = new ArrayList<String>();
    public static Connection sql = null;
    public static int totalFailures = 10;
    public static String serverTalkKey;
    public static boolean debug = false;
    public static long maxUploadSize = 26214400;
    public static boolean handleUpload = false;
    public static String uploadPath = "uploads/";
    public static boolean siteDefaults = false;
    public static boolean graphDBEnable = false;
    public static String graphDBPath = "";
    public static boolean sqlManager = false;
    public static String graphDBConfig = "";
    public static GraphDatabaseService graphDB = null;
    public static Server server = null;
    public static boolean encryptEnabled;
    public static Properties prop = new Properties();
    public static List<WSHandler> clients = new ArrayList<WSHandler>();
    public static Map<String, List<Long>> failedAttempts = new HashMap<String, List<Long>>();
    public static List<Javascript> javascripts = new ArrayList<Javascript>();
    public static ObjectWriter ow = new ObjectMapper().writer();
    public static int port = 80;
    public static boolean serverTalkEnable = false;
    public static int serverTalkPort = 8081;
    public static Level loglevel = null;
    public static String modulePath = "modules/";
    public static String configPath = "configs/";
    public static String dbPath = "databases/";

    public static void main(String[] args) {
        Log.info( "Starting Synload Development Framework Server", SynloadFramework.class );
        try {
            if ((new File("./config.ini")).exists()) {
                prop.load(new FileInputStream("config.ini"));
                port = Integer.valueOf(prop.getProperty("port"));
                handleUpload = Boolean.valueOf(prop
                        .getProperty("enableUploads"));
                siteDefaults = Boolean
                        .valueOf(prop.getProperty("siteDefaults"));
                modulePath = prop.getProperty("modulePath", modulePath);
                dbPath = prop.getProperty("dbPath", dbPath);
                configPath = prop.getProperty("configPath", configPath);
                sqlManager = Boolean.valueOf(prop.getProperty("sqlManager"));
                encryptEnabled = Boolean.valueOf(prop.getProperty("encrypt"));
                graphDBPath = prop.getProperty("graphDBPath");
                graphDBConfig = prop.getProperty("graphDBConfig");
                loglevel = Level.toLevel(prop.getProperty("loglevel"));
                debug = Boolean.valueOf(prop.getProperty("debug"));
                uploadPath = prop.getProperty("uploadPath");
                maxUploadSize = Long.valueOf(prop.getProperty("maxUploadSize"));
                serverTalkEnable = Boolean.valueOf(prop.getProperty("serverTalkEnable"));
                serverTalkKey = prop.getProperty("serverTalkKey");
                serverTalkPort = Integer.valueOf(prop.getProperty("serverTalkPort"));
                graphDBEnable = Boolean.valueOf(prop.getProperty("graphDBEnable"));
            } else {
                InputStream is = SynloadFramework.class.getClassLoader().getResourceAsStream("resources/config.ini");
                FileOutputStream os = new FileOutputStream(new File("./config.ini"));
                IOUtils.copy(is, os);
                os.close();
                is.close();
                System.exit(0);
            }
            if(!new File("./log4j.properties").exists()){
                InputStream is = SynloadFramework.class.getClassLoader().getResourceAsStream("resources/log4j.properties");
                FileOutputStream os = new FileOutputStream(new File("./log4j.properties"));
                IOUtils.copy(is, os);
                os.close();
                is.close();
            }
            if(!new File("./bbcodes.xml").exists()){
                InputStream is = SynloadFramework.class.getClassLoader().getResourceAsStream("resources/bbcodes.xml");
                FileOutputStream os = new FileOutputStream(new File("./bbcodes.xml"));
                IOUtils.copy(is, os);
                os.close();
                is.close();
            }
            Log.info("CONF", SynloadFramework.class);
            sql = DriverManager.getConnection(prop.getProperty("jdbc"),
                    prop.getProperty("dbuser"), prop.getProperty("dbpass"));

            if(sql.isClosed()){
                Log.error("MySQL failed to connect!",SynloadFramework.class);
                return;
            }
            SynloadFramework.buildDefaultHTTP();
            SynloadFramework.buildJavascript();

            createFolder(modulePath);
            createFolder(configPath);
            createFolder(dbPath);

            Log.info("Modules loading", SynloadFramework.class);

            ModuleLoader.load(modulePath);

            Log.info("Modules loaded", SynloadFramework.class);
            
            if(serverTalkEnable){
                Log.info("Server talk enabled, starting up!", SynloadFramework.class);
                new Thread (new ServerTalk()).start();
            }else{
                Log.info("Server talk system disabled, skipping", SynloadFramework.class);
            }
            if(graphDBEnable){
                Log.info("Neo4J enabled, starting up!", SynloadFramework.class);
                if (!(new File(graphDBPath)).exists()) {
                    (new File(graphDBPath)).mkdir();
                }
                graphDB = new GraphDatabaseFactory()
                    .newEmbeddedDatabaseBuilder(graphDBPath)
                    .loadPropertiesFromFile( graphDBConfig )
                    .newGraphDatabase();
            }else{
                Log.info("Neo4J disabled, skipping!", SynloadFramework.class);
            }
            
            for (Entry<String, ModuleClass> mod : ModuleRegistry
                    .getLoadedModules().entrySet()) {
                Log.info("Module ["+mod.getKey()+"] Initializing", SynloadFramework.class);
                mod.getValue().initialize();
            }

            Log.info("SQL versions", SynloadFramework.class);
            SQLRegistry.checkVersions();

            if (args.length >= 1) {
                port = Integer.valueOf(args[0]);
            }
            
            Log.info("Setting up http/websocket server", SynloadFramework.class);
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(
                    150);
            ExecutorThreadPool pool = new ExecutorThreadPool(50, 200, 10,
                    TimeUnit.MILLISECONDS, queue);
            server = new Server(pool);
            HTTPSPDYServerConnector connector = new HTTPSPDYServerConnector(
                    server);
            connector.setPort(port);
            Connector[] g = new Connector[] { connector };
            server.setConnectors(g);
            HandlerCollection handlerCollection = new HandlerCollection();
            handlerCollection.addHandler(new HTTPHandler());
            handlerCollection.addHandler(new WebsocketHandler());
            server.setHandler(handlerCollection);

            Log.info("Loaded all aspects running on port " + port,
                    SynloadFramework.class);

            server.start();
            server.join();

        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
    }
    public static void createFolder(String folderPath){
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static String randomString(int length) {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(length);
    }

    public static void buildJavascript() {
        Javascript jsUser = new Javascript();
        jsUser.addCallBack("user.msg", "recieve");
        jsUser.addCallBack("user.load", "init");
        jsUser.addCallBack("user.event", "login");
        jsUser.addCallBack("user.event", "logout");
        jsUser.addCallBack("user.event", "session");
        jsUser.addCallBack("user.event", "register");
        jsUser.setScript("/js/user.js");
        SynloadFramework.registerJavascriptFile(jsUser, "User Account System");
        Javascript jsPage = new Javascript();
        jsPage.setScript("/js/page.js");
        SynloadFramework.registerJavascriptFile(jsPage, "Page Handler");
        Javascript jsForm = new Javascript();
        jsForm.setScript("/js/form.js");
        SynloadFramework.registerJavascriptFile(jsForm, "Form Handler");
    }

    public static void buildDefaultHTTP() {

        SynloadFramework.registerHTTPPage("/", DefaultHTTPPages.class,
                "getIndex");
        if (handleUpload) {
            Log.info("Upload handler enabled!", SynloadFramework.class);
            SynloadFramework.registerHTTPPage("/system/uploads",
                    DefaultHTTPPages.class, "handleUploads");
        }
    }

    public static void broadcast(String data) {
        for (WSHandler user : SynloadFramework.clients) {
            user.send(data);
        }
    }

    public static void broadcast(List<WSHandler> _cs, String data) {
        for (WSHandler user : _cs) {
            user.send(data);
        }
    }

    public static void registerJavascriptFile(Javascript js, String name) {
        // System.out.println("[JS] Registered javascript <"+name+">");
        SynloadFramework.javascripts.add(js);
    }

    public static void registerHTTPPage(String page, Class<?> listenerClass,
            String method) {
        try {
            if (HTTPRouting.addRoutes(page, new HTTPResponse(listenerClass,
                    method))) {
                // System.out.println("[WB] Registered path <"+page+">");
            } else {
                // System.out.println("[WB][E] Failed to add <"+page+"> path");
            }
        } catch (Exception e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
    }

    public static void registerElement(WSRequest page, Class<?> listenerClass,
            String method, List<String> flags) {
        try {
            if (WSRouting.addRoutes(page, new WSResponse(listenerClass, method,
                    flags))) {
                // System.out.println("[PG] Registered page <"+page.getPri()+"> with action <"+page.getRequest()+">");
            } else {
                // System.out.println("[PG][E] Failed to add <"+page.getPri()+"> with action <"+page.getRequest()+">");
            }
        } catch (JsonProcessingException e) {
            if (SynloadFramework.debug) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, HashMap<String, Object>> getHtmlFiles() {
        return htmlFiles;
    }

    public static int getTimestamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public static void setHtmlFiles(
            HashMap<String, HashMap<String, Object>> htmlFiles) {
        SynloadFramework.htmlFiles = htmlFiles;
    }

    public static List<ModuleClass> getPlugins() {
        return plugins;
    }
    public static ModuleClass getPlugin(String plugin) {
        for(Object plug : SynloadFramework.plugins){
            if(plug.getClass().getName().equalsIgnoreCase(plugin)){
                return (ModuleClass) plug;
            }
        }
        return null;
    }
    public static void setPlugins(List<ModuleClass> plugins) {
        SynloadFramework.plugins = plugins;
    }

    public static List<String> getBannedIPs() {
        return bannedIPs;
    }

    public static void setBannedIPs(List<String> bannedIPs) {
        SynloadFramework.bannedIPs = bannedIPs;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        SynloadFramework.debug = debug;
    }

    public static long getMaxUploadSize() {
        return maxUploadSize;
    }

    public static void setMaxUploadSize(long maxUploadSize) {
        SynloadFramework.maxUploadSize = maxUploadSize;
    }

    public static boolean isHandleUpload() {
        return handleUpload;
    }

    public static void setHandleUpload(boolean handleUpload) {
        SynloadFramework.handleUpload = handleUpload;
    }

    public static boolean isSiteDefaults() {
        return siteDefaults;
    }

    public static void setSiteDefaults(boolean siteDefaults) {
        SynloadFramework.siteDefaults = siteDefaults;
    }

    public static Server getServer() {
        return server;
    }

    public static void setServer(Server server) {
        SynloadFramework.server = server;
    }

    public static Properties getProp() {
        return prop;
    }

    public static void setProp(Properties prop) {
        SynloadFramework.prop = prop;
    }

    public static List<WSHandler> getClients() {
        return clients;
    }

    public static void setClients(List<WSHandler> clients) {
        SynloadFramework.clients = clients;
    }

    public static Map<String, List<Long>> getFailedAttempts() {
        return failedAttempts;
    }

    public static void setFailedAttempts(Map<String, List<Long>> failedAttempts) {
        SynloadFramework.failedAttempts = failedAttempts;
    }

    public static List<Javascript> getJavascripts() {
        return javascripts;
    }

    public static void setJavascripts(List<Javascript> javascripts) {
        SynloadFramework.javascripts = javascripts;
    }

    public static ObjectWriter getOw() {
        return ow;
    }

    public static void setOw(ObjectWriter ow) {
        SynloadFramework.ow = ow;
    }

    public static boolean isEncryptEnabled() {
        return encryptEnabled;
    }

    public static void setEncryptEnabled(boolean encrypt) {
        SynloadFramework.encryptEnabled = encrypt;
    }

}