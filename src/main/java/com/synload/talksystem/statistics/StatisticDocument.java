package com.synload.talksystem.statistics;

import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleLoader;
import com.synload.talksystem.ConnectionDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by Nathaniel on 5/20/2016.
 */
public class StatisticDocument extends ConnectionDocument {
    public long free = 0;
    public long total = 0;
    public long max = 0;
    public int clients = 0;
    public String identifier;
    public HashMap<String, Properties> moduleProperties;
    public String modulePath;
    public String configPath;
    public String defaultPath;
    public StatisticDocument(UUID chain) {
        super(null, chain);
        free = Runtime.getRuntime().freeMemory();
        total = Runtime.getRuntime().totalMemory();
        max = Runtime.getRuntime().maxMemory();
        clients = SynloadFramework.clients.size();
        identifier = SynloadFramework.identifier;
        moduleProperties = new HashMap<String, Properties>(ModuleLoader.moduleProperties);
        modulePath = SynloadFramework.defaultPath+SynloadFramework.modulePath;
        configPath = SynloadFramework.defaultPath+SynloadFramework.configPath;
        defaultPath = SynloadFramework.defaultPath;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public HashMap<String, Properties> getModuleProperties() {
        return moduleProperties;
    }

    public void setModuleProperties(HashMap<String, Properties> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }
    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getModulePath() {

        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

}
