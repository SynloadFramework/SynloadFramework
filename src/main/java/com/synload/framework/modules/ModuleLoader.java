package com.synload.framework.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.EventTrigger;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.HandlerRegistry;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.SQLTable;
import com.synload.framework.sql.SQLRegistry;
import com.synload.framework.users.Session;
import com.synload.framework.users.User;
import com.synload.framework.ws.DefaultWSPages;

import dnl.utils.text.table.TextTable;

public class ModuleLoader extends ClassLoader {
	public static Hashtable<String, Hashtable<String,byte[]>> resources = new Hashtable<String, Hashtable<String,byte[]>>();
    public static Hashtable<String, Class<?>> cache = new Hashtable<String, Class<?>>();

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
            Class<?> c = defineClass(clazzName, clazzBytes, 0,
                    clazzBytes.length);
            cache.put(clazzName, c);
        }
    }

    public enum TYPE {
        METHOD, CLASS
    }

    @SuppressWarnings("unchecked")
    public static void load(String path) {
        String fileName;
        List<Object[]> sql = new ArrayList<Object[]>();
        List<Object[]> modules = new ArrayList<Object[]>();
        List<Object[]> events = new ArrayList<Object[]>();
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        List<String> classes = new ArrayList<String>();
        File[] listOfFiles = folder.listFiles();
        Class<?> loadedClass;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();
                if (fileName.endsWith(".jar")) {
                	loadModuleFiles(classes, sql, modules, events, path, fileName, true, true);
                }
            }
        }
        
        /*
         * SynloadFramework defaults!
         */
        try {
        	String[] SynJar = SynloadFramework.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString().split("/");
			loadModuleFiles(classes, sql, modules, events, "lib/", SynJar[SynJar.length-1], true, false);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        
        /*
         * Hardcoded defaults!
         */
        try {
            events.addAll((List<Object[]>) register(DefaultWSPages.class, Handler.EVENT, TYPE.METHOD, null)[0]);
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
        }
        /*
         * Defaults setting end
         * 
         * Display tables of data
         * 
         */
        display( sql, modules, events );
        
    }
    /*
     * load module jars files
     * 
     */
    public static void loadModuleFiles(List<String> classes, List<Object[]> sql, List<Object[]> modules, List<Object[]> events, String path, String fileName, boolean loadResources, boolean loadClasses){
    	Class<?> loadedClass;
    	Log.info("Loaded file: "+path+fileName, ModuleLoader.class);
    	try {
            URLClassLoader cl = new URLClassLoader(new URL[] { new File(path+fileName).toURI().toURL() });
            
            Properties moduleSettings = new Properties();
            InputStream is = cl.getResourceAsStream("module.ini");
            String moduleName = "";
            if (is != null) {
            	moduleSettings.load(is);
            	moduleName = moduleSettings.getProperty("module");
            	Log.info("Module Name: "+moduleName, ModuleLoader.class);
            	is.close();
            }
            if(moduleName.equals("ws")){
            	Log.error("Error module name is reserved 'ws'", ModuleLoader.class);
            	System.exit(-1);
            }
            if(!resources.containsKey(moduleName)){
    			resources.put(moduleName, new Hashtable<String,byte[]>() );
    		}
            HashMap<String,List<String>> resourcesList = getClasses(path + fileName);
            if(resourcesList.containsKey("classes") && loadClasses==true){
                for (String clazz : resourcesList.get("classes")) {
                    String clazzFile = clazz.replaceAll("(?i)\\.", "/") + ".class";
                    Log.debug("Loading class data for " + clazz + "[R:{" + clazzFile + "}]", ModuleLoader.class);
                    is = cl.getResourceAsStream(clazzFile);
                    if (is == null) {
                        Log.error("Resource Error for " + clazz + "[R:{" + clazzFile + "}]", ModuleLoader.class);
                    }
                    byte[] clazzBytes = IOUtils.toByteArray(is);
                    is.close();
                    ModuleLoader ml = new ModuleLoader(Thread.currentThread().getContextClassLoader());
                    ml.loadClass(clazz, clazzBytes); // load class bytes
                    classes.add(clazz);
                    loadedClass = (new ModuleLoader(Thread.currentThread().getContextClassLoader())).loadClass(clazz); // load class
                    ModuleClass module = null;
                    Object[] obj = register(loadedClass, Handler.MODULE, TYPE.CLASS, null);
                    if (obj != null) {
                        module = (ModuleClass) obj[0];
                        modules.add((Object[]) obj[1]);
                    }
                    Object[] obsql = registerSQL(loadedClass, module); 
                    if (obsql != null) {
                        sql.add(obsql);
                    }
                    events.addAll((List<Object[]>) register(loadedClass, Handler.EVENT, TYPE.METHOD, module)[0]);
                }
            }
            if(resourcesList.containsKey("resources") && loadResources==true){
            	for(String resource : resourcesList.get("resources")){
            		is = cl.getResourceAsStream(resource);
            		if (is == null) {
            			Log.error("Resource Error for " + resource + "[R:{" + resource + "}]", ModuleLoader.class);
            		}
            		byte[] resourceBytes = IOUtils.toByteArray(is);
            		resources.get(moduleName).put(resource.replace("www/", ""), resourceBytes);
            		is.close();
            	}
            }
            cl.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    }

    public static void display( List<Object[]> sql, List<Object[]> modules, List<Object[]> events ){
    	System.out.println("\nModules Loaded");
        TextTable tt = new TextTable(new String[] { "Class", "Name", "Author", "Version" }, modules.toArray(new Object[modules.size()][]));
        tt.printTable();
        System.out.println("\nEvents Loaded");
        tt = new TextTable(new String[] { "Class", "Module", "Method Name", "Type", "Description", "Trigger" }, events.toArray(new Object[events.size()][]));
        tt.printTable();
        System.out.println("\nSQL Tables Loaded");
        tt = new TextTable(new String[] { "Class", "Name", "Description", "Version" }, sql.toArray(new Object[sql.size()][]));
        tt.printTable();
        System.out.print("\n");
    }
    public static <T> Object[] registerSQL(Class<T> c, ModuleClass module) {
        if (c.isAnnotationPresent(SQLTable.class)) {
            SQLTable tbl = c.getAnnotation(SQLTable.class);
            Object[] obj = new Object[4];
            obj[0] = c.getName();
            obj[1] = tbl.name();
            obj[2] = tbl.description();
            obj[3] = tbl.version();
            SQLRegistry.register(c);
            return obj;
        }
        return null;
    }

    /*
     * Checks for Addons, Methods in each class
     */

    @SuppressWarnings("unchecked")
    public static <T> Object[] register(Class<T> c, Handler annotationClass,
            TYPE type, ModuleClass module) throws InstantiationException,
            IllegalAccessException {
        if (TYPE.CLASS == type) {
            if (c.isAnnotationPresent(annotationClass.getAnnotationClass())) {
                /*
                 * Loaded a module, declare it as such and register it!
                 */
                Object[] obj = new Object[4];
                Module moduleAnnotation = (Module) c
                        .getAnnotation(Handler.MODULE.getAnnotationClass());
                ModuleClass mod = (ModuleClass) c.newInstance();
                
                SynloadFramework.plugins.add(mod);
                
                ModuleRegistry.getLoadedModules().put(moduleAnnotation.name(),
                        mod);
                obj[0] = c.getName();
                obj[1] = moduleAnnotation.name();
                obj[2] = moduleAnnotation.author();
                obj[3] = moduleAnnotation.version();
                // mod.initialize();
                return new Object[] { mod, obj };
            }
        } else if (TYPE.METHOD == type) {
            List<Object[]> obj = new ArrayList<Object[]>();
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(annotationClass.getAnnotationClass())) {
                    EventTrigger et = new EventTrigger();

                    Event eventAnnotation = (Event) m
                            .getAnnotation(Handler.EVENT.getAnnotationClass());
                    if (eventAnnotation.enabled()) {
                        et.setHostClass(c);
                        et.setMethod(m);
                        et.setModule(module);
                        et.setTrigger(eventAnnotation.trigger());
                        et.setFlags(eventAnnotation.flags());
                        et.setEventType(eventAnnotation.type());

                        Object[] obj_tmp = new Object[6];
                        if (module != null) {
                            Module modul = (Module) module
                                    .getClass()
                                    .getAnnotation(
                                            Handler.MODULE.getAnnotationClass());
                            obj_tmp[1] = modul.name();
                        } else {
                            obj_tmp[1] = "";
                        }
                        obj_tmp[0] = c.getName();
                        obj_tmp[2] = m.getName();
                        obj_tmp[3] = eventAnnotation.type();
                        obj_tmp[4] = eventAnnotation.description();
                        try {
                            obj_tmp[5] = SynloadFramework.ow
                                    .writeValueAsString(eventAnnotation
                                            .trigger());
                        } catch (JsonProcessingException e1) {
                            e1.printStackTrace();
                        }
                        obj.add(obj_tmp);
                        HandlerRegistry.register(
                                annotationClass.getAnnotationClass(), et);
                    }
                }
            }
            return new Object[] { obj };
        }
        return null;
    }

    @SuppressWarnings("resource")
    public static HashMap<String,List<String>> getClasses(String file) throws IOException {
    	
    	HashMap<String,List<String>> returnedData = new HashMap<String,List<String>>();
        List<String> classNames = new ArrayList<String>();
        List<String> resources = new ArrayList<String>();
        
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
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
                classNames.add(className.toString());
            }else if(entry.getName().contains("www/")){
            	resources.add(entry.getName());
            }
        }
        returnedData.put("classes", classNames);
        returnedData.put("resources", resources);
        return returnedData;
    }
}
