package com.synload.framework.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.EventTrigger;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.HandlerRegistry;
import com.synload.eventsystem.Type;
import com.synload.eventsystem.events.annotations.Event;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.http.HTTPRegistry;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.sql.SQLTable;
import com.synload.framework.sql.SQLRegistry;
import com.synload.framework.ws.annotations.Perms;
import com.synload.framework.ws.annotations.WSEvent;

import dnl.utils.text.table.TextTable;

public class ModuleLoader extends ClassLoader {
	public static Thread checkNewJar=null;
	public static Hashtable<String, Hashtable<String,byte[]>> resources = new Hashtable<String, Hashtable<String,byte[]>>();
    public static Hashtable<String, Class<?>> cache = new Hashtable<String, Class<?>>();
    public static HashMap<String, String> loadedModules = new HashMap<String, String>();
    
    public static HashMap<String, ModuleData> jarList = new HashMap<String, ModuleData>();
    
    
    public static List<String> modules = new ArrayList<String>();
    public ModuleLoader(ClassLoader parent) {
        super(parent);
    }

    public synchronized Class<?> loadClass(String clazzName) {
        Class<?> c = cache.get(clazzName);
        if (c == null) {
            try {
                c = Class.forName(clazzName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    public void loadClass(String clazzName, byte[] clazzBytes) {
        try {
            Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            Class<?> c = defineClass(clazzName, clazzBytes, 0, clazzBytes.length);
            cache.put(clazzName, c);
        }
    }

    public enum TYPE {
        METHOD, CLASS
    }

    @SuppressWarnings("unchecked")
    public static void load(String path) {
        String fileName;
        if(checkNewJar==null){
			checkNewJar = new Thread(new CheckNewJar(path));
			checkNewJar.start();
		}
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        List<Object[]> sql = new ArrayList<Object[]>();
        List<Object[]> modules = new ArrayList<Object[]>();
        List<Object[]> events = new ArrayList<Object[]>();
        List<String> classes = new ArrayList<String>();
        HashMap<String,String> toLoadList = new HashMap<String,String>();
        File[] listOfFiles = folder.listFiles();
        Class<?> loadedClass;
        /*
         * SynloadFramework defaults!
         */
        try {
            String[] SynJar = SynloadFramework.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString().split("/");
            loadModuleFiles("lib/", SynJar[SynJar.length-1], true, false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();
                if (fileName.endsWith(".jar")) {
                	InputStream hashIS = null;
                    try {
                    	hashIS = new FileInputStream(new File(path+fileName));
        				loadedModules.put(fileName, SHA256(IOUtils.toByteArray(hashIS)));
        			} catch (NoSuchAlgorithmException e) { 
        				e.printStackTrace();
        			} catch (IOException e) {
						e.printStackTrace();
					}finally{
						if(hashIS!=null){
							try {
								hashIS.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
                	loadModuleFiles( path, fileName, true, true);
                }
            }
        }
        for(Entry<String, ModuleData> clazz : jarList.entrySet()){
        	for(String clazzPath : clazz.getValue().getClasses()){
				try {
					loadedClass = (new ModuleLoader(Thread.currentThread().getContextClassLoader())).loadClass(clazzPath); // load class
		            ModuleClass module = null;
		            Object[] obj = register(loadedClass, Handler.MODULE, TYPE.CLASS, null, clazz.getValue());
					if (obj != null) {
		                module = (ModuleClass) obj[0];
		                modules.add((Object[]) obj[1]);
		            }
		            Object[] obsql = registerSQL(loadedClass, clazz.getValue().getName()); 
		            if (obsql != null) {
		                sql.add(obsql);
		            }
		            events.addAll((List<Object[]>) register(loadedClass, Handler.EVENT, TYPE.METHOD, module, clazz.getValue())[0]);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }
        /*
         * Hardcoded defaults!
         */
        /*try {
            events.addAll((List<Object[]>) register(DefaultWSPages.class, Handler.EVENT, TYPE.METHOD, null, "SynloadFramework")[0]);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Object[] obsql = registerSQL(User.class, null);
        if (obsql != null) {
            sql.add(obsql);
        }
        obsql = registerSQL(Session.class, null);
        if (obsql != null) {
            sql.add(obsql);
        }*/
        /*
         * Defaults setting end
         * 
         * Display tables of data
         * 
         */
        display( sql, modules, events );
        
    }
    
    public static String SHA256(byte[] convertme) throws NoSuchAlgorithmException{
    	byte[] mdbytes = MessageDigest.getInstance("SHA-256").digest(convertme);
    	StringBuffer hexString = new StringBuffer();
    	for (int i=0;i<mdbytes.length;i++) {
    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
    	}
        return hexString.toString();
    }
    
    public static void unload(ModuleData module){
    	
    	/*ModuleData jarM = jar.get(fileName);
		ModuleRegistry.getLoadedModules().remove(jarM.getName());
		SynloadFramework.plugins.remove(jmod[1]);
    	List<Object[]> jarE = jarEvents.get(fileName);
    	for(Object[] jE : jarE){
    		HandlerRegistry.unregister((Class<?>)jE[0], (EventTrigger)jE[1]);
    	}
    	List<String> jarC = jar.get(fileName).getResources();
    	for(String jC : jarC){
    		cache.remove(jC);
    	}*/
    	
    }
    
    /*
     * load module jars files
     * 
     */
    public static ModuleData loadModuleFiles(String path, String fileName, boolean loadResources, boolean loadClasses){
    	Log.info("Loaded file: "+path+fileName, ModuleLoader.class);
    	try {
            ModuleData moduleData = getJarData(path + fileName);
            ModuleLoader.jarList.put(path + fileName, moduleData);
            return moduleData;
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return null;
    }

    public static void display( List<Object[]> sql, List<Object[]> modules, List<Object[]> events ){
    	System.out.println("\nModules Loaded");
        TextTable tt = new TextTable(new String[] { "Class", "Name", "Author", "Version" }, modules.toArray(new Object[modules.size()][]));
        tt.printTable();
        System.out.println("\nEvents Loaded");
        tt = new TextTable(new String[] { "Class", "Module", "Method Name", "Type", "Description", "Trigger" }, events.toArray(new Object[events.size()][]));
        tt.printTable();
        System.out.println("\nSQL Tables Loaded");
        tt = new TextTable(new String[] { "Class", "Module", "Name", "Description", "Version" }, sql.toArray(new Object[sql.size()][]));
        tt.printTable();
        System.out.print("\n");
    }
    public static <T> Object[] registerSQL(Class<T> c, String moduleName) {
        if (c.isAnnotationPresent(SQLTable.class)) {
            SQLTable tbl = c.getAnnotation(SQLTable.class);
            Object[] obj = new Object[5];
            obj[0] = c.getName();
            obj[1] = moduleName;
            obj[2] = tbl.name();
            obj[3] = tbl.description();
            obj[4] = tbl.version();
            SQLRegistry.register(c);
            return obj;
        }
        return null;
    }
    

    /*
     * Checks for Addons, Methods in each class
     */
    @SuppressWarnings("unchecked")
    public static <T> Object[] register(Class<T> c, Handler annotationClass, TYPE type, ModuleClass module, ModuleData moduleData) throws InstantiationException, IllegalAccessException {
        if (TYPE.CLASS == type) {
            if (c.isAnnotationPresent(annotationClass.getAnnotationClass())) {
                /*
                 * Loaded a module, declare it as such and register it!
                 */
                Object[] obj = new Object[4];
                Module moduleAnnotation = (Module) c.getAnnotation(Handler.MODULE.getAnnotationClass());
                ModuleClass mod = (ModuleClass) c.newInstance();
                
                SynloadFramework.plugins.add(mod);
                
                ModuleRegistry.getLoadedModules().put(moduleAnnotation.name(), mod);
                obj[0] = c.getName();
                obj[1] = moduleAnnotation.name();
                obj[2] = moduleAnnotation.author();
                obj[3] = moduleAnnotation.version();
                moduleData.setVersion(moduleAnnotation.version());
                // mod.initialize();
                return new Object[] { mod, obj };
            }
        } else if (TYPE.METHOD == type) {
            List<Object[]> obj = new ArrayList<Object[]>();
            for (Method m : c.getMethods()) {
            	HTTPRegistry.moduleLoad(c, m); // load http requests on methods
                if (m.isAnnotationPresent(WSEvent.class)) {
                    EventTrigger et = new EventTrigger();

                    WSEvent eventAnnotation = (WSEvent) m.getAnnotation(WSEvent.class);
                    String[] flags = new String[]{};
                    if(m.isAnnotationPresent(Perms.class)){
                    	Perms perm = m.getAnnotation(Perms.class);
                    	flags = perm.value();
                    }
                    if (eventAnnotation.enabled()) {
                        et.setHostClass(c);
                        et.setMethod(m);
                        et.setModule(module);
                        et.setTrigger(new String[]{eventAnnotation.method(), eventAnnotation.action()});
                        et.setFlags(flags);
                        et.setEventType(Type.WEBSOCKET);

                        Object[] obj_tmp = new Object[6];
                        obj_tmp[0] = c.getName();
                        obj_tmp[1] = moduleData.getName();
                        obj_tmp[2] = m.getName();
                        obj_tmp[3] = Type.WEBSOCKET;
                        obj_tmp[4] = eventAnnotation.description();
                        try {
                            obj_tmp[5] = SynloadFramework.ow.writeValueAsString(new String[]{eventAnnotation.method(), eventAnnotation.action()});
                        } catch (JsonProcessingException e1) {
                            e1.printStackTrace();
                        }
                        obj.add(obj_tmp);
                        HandlerRegistry.register(annotationClass.getAnnotationClass(), et);
                    }
                }else if(m.isAnnotationPresent(Event.class)){
                	Event eventAnnotation = (Event) m.getAnnotation(Event.class);
                	EventTrigger et = new EventTrigger();
                	if (eventAnnotation.enabled()) {
                        et.setHostClass(c);
                        et.setMethod(m);
                        et.setModule(module);
                        et.setTrigger(new String[]{});
                        et.setFlags(new String[]{});
                        et.setEventType(Type.OTHER);

                        Object[] obj_tmp = new Object[6];
                        obj_tmp[0] = c.getName();
                        obj_tmp[1] = moduleData.getName();
                        obj_tmp[2] = m.getName();
                        obj_tmp[3] = Type.OTHER;
                        obj_tmp[4] = eventAnnotation.description();
                        try {
                            obj_tmp[5] = SynloadFramework.ow.writeValueAsString(new String[]{});
                        } catch (JsonProcessingException e1) {
                            e1.printStackTrace();
                        }
                        obj.add(obj_tmp);
                        HandlerRegistry.register(annotationClass.getAnnotationClass(), et);
                    }
                }
            }
            return new Object[] { obj };
        }
        return null;
    }

    public static boolean addClassByteArray(String zip, String filename, String moduleName, String className, byte[] buffer){
    	ModuleLoader ml = new ModuleLoader(Thread.currentThread().getContextClassLoader());
        ml.loadClass(className, buffer);
    	return false;
    }
    
    public static boolean addResourceByteArray(String zip, String file, String moduleName, byte[] buffer){
    	//Log.info(file, ModuleLoader.class);
    	resources.get(moduleName).put(file.replace("www/", ""), buffer);
    	return false;
    }
    
    @SuppressWarnings("resource")
    public static ModuleData getJarData(String file) throws IOException{
    	ModuleData mData = new ModuleData();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        Properties moduleSettings = new Properties();
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
        	if(entry.getName().contains("module.ini")){
        		byte[] buffer = new byte[(int)entry.getSize()];
            	IOUtils.readFully(zip, buffer);
            	InputStream is = IOUtils.toInputStream(new String(buffer));
            	moduleSettings.load(is);
            	is.close();
        	}
        }
        zip.close();
        
        String moduleName = moduleSettings.getProperty("module");
        mData.setName(moduleName);
        mData.setFile(file);
        if(moduleName.equals("ws")){
        	Log.error("Error module name is reserved 'ws'", ModuleLoader.class);
        	System.exit(-1);
        }
        resources.put(moduleName, new Hashtable<String,byte[]>());
        zip = new ZipInputStream(new FileInputStream(file));
		try {
	        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
	            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
	            	byte[] buffer = new byte[(int)entry.getSize()];
	            	IOUtils.readFully(zip, buffer);
	            	StringBuilder className = new StringBuilder();
	                for (String part : entry.getName().split("/")) {
	                    if (className.length() != 0){
	                        className.append(".");
	                    }
	                    className.append(part);
	                    if (part.endsWith(".class")){
	                        className.setLength(className.length()- ".class".length());
	                    }
	                }
	            	addClassByteArray(file, entry.getName(), moduleName, className.toString(), buffer);
	            	mData.getClasses().add(className.toString());
	            }else if(entry.getName().contains("www/") && !entry.isDirectory()){
	            	byte[] buffer = new byte[(int)entry.getSize()];
	            	IOUtils.readFully(zip, buffer);
	            	addResourceByteArray(file, entry.getName(), moduleName, buffer);
	            	mData.getResources().add(entry.getName());
	            }
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			try{
                zip.close();
            }catch(Exception e){

            }
		}
        return mData;
    }
}
