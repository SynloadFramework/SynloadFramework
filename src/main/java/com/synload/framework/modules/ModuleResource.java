package com.synload.framework.modules;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;

/**
 * Created by Nathaniel on 5/14/2016.
 */
public class ModuleResource {
    public static Hashtable<String, Hashtable<String,byte[]>> resources = new Hashtable<String, Hashtable<String,byte[]>>();
    public static boolean moduleExists(String module){
        return resources.containsKey(module);
    }
    public static boolean fileExists(String module, String file){
        if(moduleExists(module))
            return resources.get(module).containsKey(file);
        return false;
    }
    public static byte[] get(String module, String file){
        if(module.contains("..") || file.contains("..")) {
            return null;
        }
        File baseDir = new File(module).getAbsoluteFile();
        File f = new File(module+"/"+file);
        try {
            String canonicalBase = baseDir.getCanonicalPath();
            String canonicalFile = f.getCanonicalPath();
            if(!canonicalFile.startsWith(canonicalBase + File.separator) && !canonicalFile.equals(canonicalBase)) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        if(f.exists()){
            try {
                return FileUtils.readFileToByteArray(f);
            }catch(Exception e){
                return null;
            }
        }else{
            if(resources.containsKey(module) && resources.get(module).containsKey(file)){
                return resources.get(module).get(file);
            }
        }
        return null;
    }

    public static Hashtable<String, Hashtable<String, byte[]>> getResources() {
        return resources;
    }
}
