package com.synload.framework.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.synload.eventsystem.Handler;
import com.synload.framework.Log;
import com.synload.framework.modules.ModuleLoader.TYPE;

public class CheckNewJar implements Runnable{
	public String path="";
	public CheckNewJar(String path){
		this.path = path;
	}
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Object[]> sql = new ArrayList<Object[]>();
	        List<Object[]> modules = new ArrayList<Object[]>();
	        List<Object[]> events = new ArrayList<Object[]>();
	        List<String> classes = new ArrayList<String>();
	        
	        List<ModuleData> toLoadList = new ArrayList<ModuleData>();
	        
			File folder = new File(path);
			boolean anythingNew=false;
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
	            if (listOfFiles[i].isFile()) {
	            	String fileName = listOfFiles[i].getName();
	            	if (fileName.endsWith(".jar")) {
	            		if(!ModuleLoader.loadedModules.containsKey(fileName)){
	            			// LOAD NEW MODULE
	            			Log.info("Found new module, loading "+fileName, CheckNewJar.class);
	            			ModuleData md = ModuleLoader.loadModuleFiles(path, fileName, true, true);
	            			InputStream hashIS = null;
		                    try {
		                    	hashIS = new FileInputStream(new File(path+fileName));
		                    	toLoadList.add(md);
		                    	ModuleLoader.loadedModules.put(fileName, ModuleLoader.SHA256(IOUtils.toByteArray(hashIS)));
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
	            			anythingNew=true;
	            		}else{
		            		InputStream hashIS = null;
		                    try {
		                    	hashIS = new FileInputStream(new File(path+fileName));
		                    	String hash = ModuleLoader.SHA256(IOUtils.toByteArray(hashIS));
		                    	if(!ModuleLoader.loadedModules.get(fileName).equals(hash)){
		                    		anythingNew=true;
		                    		Log.info("Found change to "+fileName, CheckNewJar.class);
		                    		// RELOAD MODULE
		                    		ModuleData md = ModuleLoader.loadModuleFiles(path, fileName, true, true);
		                    		toLoadList.add(md);
		                    	}
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
	            		}
	            	}
	            }
			}
			if(anythingNew){
				for(ModuleData modData : toLoadList){
					ModuleLoader.unload(modData);
					for(String clazz: modData.getClasses()){
						try {
							Class<?> loadedClass = (new ModuleLoader(Thread.currentThread().getContextClassLoader())).loadClass(clazz); // load class
				            ModuleClass module = null;
				            Object[] obj = ModuleLoader.register(loadedClass, Handler.MODULE, TYPE.CLASS, null, modData.getName());
							if (obj != null) {
				                module = (ModuleClass) obj[0];
				                modules.add((Object[]) obj[1]);
				            }
				            Object[] obsql = ModuleLoader.registerSQL(loadedClass, modData.getName()); 
				            if (obsql != null) {
				                sql.add(obsql);
				            }
				            events.addAll((List<Object[]>) ModuleLoader.register(loadedClass, Handler.EVENT, TYPE.METHOD, module, modData.getName())[0]);
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
		        }
				ModuleLoader.display( sql, modules, events );
			}
		}
	}
	
}
