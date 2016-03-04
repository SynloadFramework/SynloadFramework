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
			List<Object[]> sql = new ArrayList<Object[]>();
	        List<Object[]> modules = new ArrayList<Object[]>();
	        List<Object[]> events = new ArrayList<Object[]>();
	        List<String> classes = new ArrayList<String>();
	        
	        HashMap<String,String> toLoadList = new HashMap<String,String>();
	        
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
	            			ModuleLoader.loadModuleFiles(toLoadList, classes, sql, modules, events, path, fileName, true, true);
	            			InputStream hashIS = null;
		                    try {
		                    	hashIS = new FileInputStream(new File(path+fileName));
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
	            			anythingNew=true;
		            		InputStream hashIS = null;
		                    try {
		                    	hashIS = new FileInputStream(new File(path+fileName));
		                    	Log.info(ModuleLoader.loadedModules.get(fileName)+"="+ModuleLoader.SHA256(IOUtils.toByteArray(hashIS)), CheckNewJar.class);
		                    	if(ModuleLoader.loadedModules.get(fileName) != ModuleLoader.SHA256(IOUtils.toByteArray(hashIS))){
		                    		Log.info("Found change to "+fileName, CheckNewJar.class);
		                    		// RELOAD MODULE
		                    		ModuleLoader.unload(fileName);
		                    		ModuleLoader.loadModuleFiles(toLoadList, classes, sql, modules, events, path, fileName, true, true);
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
				for(Entry<String, String> clazz : toLoadList.entrySet()){
					try {
						Class<?> loadedClass = (new ModuleLoader(Thread.currentThread().getContextClassLoader())).loadClass(clazz.getKey()); // load class
			            ModuleClass module = null;
			            Object[] obj = ModuleLoader.register(clazz.getValue(), loadedClass, Handler.MODULE, TYPE.CLASS, null);
						if (obj != null) {
			                module = (ModuleClass) obj[0];
			                modules.add((Object[]) obj[1]);
			            }
			            Object[] obsql = ModuleLoader.registerSQL(loadedClass, module); 
			            if (obsql != null) {
			                sql.add(obsql);
			            }
			            events.addAll((List<Object[]>) ModuleLoader.register(clazz.getValue(), loadedClass, Handler.EVENT, TYPE.METHOD, module)[0]);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
		            
		        }
				ModuleLoader.display( sql, modules, events );
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
