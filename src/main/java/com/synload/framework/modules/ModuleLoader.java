package com.synload.framework.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.EventTrigger;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.HandlerRegistry;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.SQLTable;
import com.synload.framework.sql.SQLRegistry;
import com.synload.framework.users.Session;
import com.synload.framework.users.User;
import com.synload.framework.ws.DefaultWSPages;

import dnl.utils.text.table.TextTable;


public class ModuleLoader {
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
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();
                if (fileName.endsWith(".jar")) {
                    try {
                        @SuppressWarnings("resource")
						URLClassLoader cl = new URLClassLoader(
                                new URL[] { new File(path + fileName).toURI()
                                        .toURL() });
                        List<String> classList = getClasses(path + fileName);
                        ModuleClass module = null;
                        for (String clazz : classList) {
                            try {
                                @SuppressWarnings("rawtypes")
								Class loadedClass = cl.loadClass(clazz);
                                try {
                                	Object[] obj = register(loadedClass, Handler.MODULE, TYPE.CLASS, null);
                                	if(obj!=null){
                                		module = (ModuleClass) obj[0];
                                		modules.add((Object[]) obj[1]);
                                	}
                                	Object[] obsql = registerSQL(loadedClass, module);
                                	if(obsql!=null){
                                		sql.add(obsql);
                                	}
                                    events.addAll( (List<Object[]>) register(loadedClass, Handler.EVENT, TYPE.METHOD, module)[0] );
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                // cl.close();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        /* 
         * Hardcoded defaults!
         */
		try {
			events.addAll( (List<Object[]>) register(DefaultWSPages.class, Handler.EVENT, TYPE.METHOD, null)[0]);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Object[] obsql = registerSQL(User.class, null);
        if(obsql!=null){
        	sql.add(obsql);
        }
    	obsql = registerSQL(Session.class, null);
    	if(obsql!=null){
    		sql.add(obsql);
    	}
    	/*
    	 * Defaults setting end
    	 */
    	
        System.out.println("\nModules Loaded");
        TextTable tt = new TextTable(new String[]{"Class", "Name", "Author", "Version"}, modules.toArray(new Object[modules.size()][]));
        tt.printTable();
        System.out.println("\nEvents Loaded");
        tt = new TextTable(new String[]{"Class", "Module", "Method Name", "Type", "Description", "Trigger"}, events.toArray(new Object[events.size()][]));
        tt.printTable();
        System.out.println("\nSQL Tables Loaded");
        tt = new TextTable(new String[]{"Class", "Name", "Description", "Version"}, sql.toArray(new Object[sql.size()][]));
        tt.printTable();
        System.out.print("\n");
    }
    
	public static <T> Object[] registerSQL( Class<T> c, ModuleClass module ){
        if (c.isAnnotationPresent(SQLTable.class)){
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
	public static <T> Object[] register( Class<T> c,
            Handler annotationClass, TYPE type, ModuleClass module)
            throws InstantiationException, IllegalAccessException {
        if (TYPE.CLASS == type) {
            if (c.isAnnotationPresent(annotationClass.getAnnotationClass())) {
                /*
                 * Loaded a module, declare it as such and register it!
                 */
            	Object[] obj = new Object[4];
                Module moduleAnnotation = (Module) c
                        .getAnnotation(Handler.MODULE.getAnnotationClass());
                ModuleClass mod = (ModuleClass) c.newInstance();
                ModuleRegistry.getLoadedModules().put(moduleAnnotation.name(),
                        mod);
                obj[0] = c.getName();
                obj[1] = moduleAnnotation.name();
                obj[2] = moduleAnnotation.author();
                obj[3] = moduleAnnotation.version();
                //mod.initialize();
                return new Object[]{mod,obj};
            }
        } else if (TYPE.METHOD == type) {
        	List<Object[]> obj = new ArrayList<Object[]>();
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(annotationClass.getAnnotationClass())) {
                    EventTrigger et = new EventTrigger();
                    
                    Event eventAnnotation = (Event) m.getAnnotation(Handler.EVENT.getAnnotationClass());
                    if(eventAnnotation.enabled()){
	                    et.setHostClass(c);
	                    et.setMethod(m);
	                    et.setModule(module);
	                    et.setTrigger(eventAnnotation.trigger());
	                    et.setFlags(eventAnnotation.flags());
	                    et.setEventType(eventAnnotation.type());
	                    
	                    Object[] obj_tmp = new Object[6];
	                    if(module!=null){
	                    	Module modul = (Module) module.getClass().getAnnotation(Handler.MODULE.getAnnotationClass());
	                    	obj_tmp[1] = modul.name();
	                    }else{
	                    	obj_tmp[1] = "";
	                    }
	                    obj_tmp[0] = c.getName();
	                    obj_tmp[2] = m.getName();
	                    obj_tmp[3] = eventAnnotation.type();
	                    obj_tmp[4] = eventAnnotation.description();
	                    try {
							obj_tmp[5] = SynloadFramework.ow.writeValueAsString(eventAnnotation.trigger());
						} catch (JsonProcessingException e1) {
							e1.printStackTrace();
						}
	                    obj.add(obj_tmp);
	                    HandlerRegistry.register(
	                            annotationClass.getAnnotationClass(), et);
                    }
                }
            }
            return new Object[]{obj};
        }
        return null;
    }

    @SuppressWarnings("resource")
	public static List<String> getClasses(String file) throws IOException {
        List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip
                .getNextEntry())
            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                // This ZipEntry represents a class. Now, what class does it
                // represent?
                StringBuilder className = new StringBuilder();
                for (String part : entry.getName().split("/")) {
                    if (className.length() != 0)
                        className.append(".");
                    className.append(part);
                    if (part.endsWith(".class"))
                        className.setLength(className.length()
                                - ".class".length());
                }
                classNames.add(className.toString());
            }
        return classNames;
    }
}
