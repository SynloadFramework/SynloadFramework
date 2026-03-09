package com.synload.framework.modules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleRegistry {
    private static Map<String, ModuleClass> loadedModules = new ConcurrentHashMap<String, ModuleClass>();

    public static Map<String, ModuleClass> getLoadedModules() {
        return loadedModules;
    }

    public static void setLoadedModules(Map<String, ModuleClass> loadedModules) {
        ModuleRegistry.loadedModules = loadedModules;
    }

    public static ModuleClass get(String module) {
        return loadedModules.get(module);
    }

}
