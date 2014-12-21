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

public class ModuleLoader {
    public enum TYPE {
        METHOD, CLASS
    }

    public static void load(String path) {
        String fileName;
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
                                    ModuleClass tmp = register(
                                            loadedClass, Handler.MODULE,
                                            TYPE.CLASS, null);
                                    if (tmp != null)
                                        module = tmp;
                                    register(loadedClass,
                                            Handler.EVENT, TYPE.METHOD, module);
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
    }

    /*
     * Checks for Addons, Methods in each class
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ModuleClass register( Class c,
            Handler annotationClass, TYPE type, ModuleClass module)
            throws InstantiationException, IllegalAccessException {
        if (TYPE.CLASS == type) {
            if (c.isAnnotationPresent(annotationClass.getAnnotationClass())) {
                /*
                 * Loaded a module, declare it as such and register it!
                 */
                Module moduleAnnotation = (Module) c
                        .getAnnotation(Handler.MODULE.getAnnotationClass());
                ModuleClass mod = (ModuleClass) c.newInstance();
                ModuleRegistry.getLoadedModules().put(moduleAnnotation.name(),
                        mod);
                System.out.println("[INFO] Loaded module: "
                        + moduleAnnotation.name());
                System.out.println("[INFO] Author: "
                        + moduleAnnotation.author());
                mod.initialize();
                return mod;
            }
        } else if (TYPE.METHOD == type) {
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
	                    System.out.println("[INFO] \tEvent Registered ["+eventAnnotation.type()+"]");
	                    System.out.println("[INFO] Loaded Method: " + m.getName());
	                    System.out.println("[INFO] \tDescription: " + eventAnnotation.description());
	                    try {
							System.out.println("[INFO] \tTrigger: " + SynloadFramework.ow.writeValueAsString(eventAnnotation.trigger()));
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    HandlerRegistry.register(
	                            annotationClass.getAnnotationClass(), et);
                    }
                }
            }
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
