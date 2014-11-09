package com.synload.framework.modules;

import java.util.HashMap;
import java.util.Map;

public class ModuleRegistry {
    private static Map<String, ModuleClass> loadedModules = new HashMap<String, ModuleClass>();

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
