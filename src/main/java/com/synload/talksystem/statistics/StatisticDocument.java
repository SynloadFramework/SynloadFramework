package com.synload.talksystem.statistics;

import com.synload.framework.SynloadFramework;
import com.synload.framework.TransmissionStats;
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
    public long ws_sent = 0;
    public long ws_receive = 0;
    public long http_sent = 0;
    public long http_receive = 0;
    public String identifier;
    public HashMap<String, Properties> moduleProperties;
    public String modulePath;
    public String configPath;
    public String defaultPath;
    public Properties instanceProperties;
    public StatisticDocument(UUID chain) {
        super(null, chain);
        ws_sent = TransmissionStats.ws_sent;
        ws_receive = TransmissionStats.ws_receive;
        http_receive = TransmissionStats.http_receive;
        http_sent = TransmissionStats.http_sent;
        free = Runtime.getRuntime().freeMemory();
        total = Runtime.getRuntime().totalMemory();
        max = Runtime.getRuntime().maxMemory();
        clients = SynloadFramework.clients.size();
        identifier = SynloadFramework.identifier;
        moduleProperties = new HashMap<String, Properties>(ModuleLoader.moduleProperties);
        modulePath = SynloadFramework.modulePath;
        configPath = SynloadFramework.configPath;
        defaultPath = SynloadFramework.defaultPath;
        instanceProperties = SynloadFramework.getProp();
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

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public Properties getInstanceProperties() {
        return instanceProperties;
    }

    public void setInstanceProperties(Properties instanceProperties) {
        this.instanceProperties = instanceProperties;
    }

    public long getHttp_receive() {
        return http_receive;
    }

    public void setHttp_receive(long http_receive) {
        this.http_receive = http_receive;
    }

    public long getWs_sent() {
        return ws_sent;
    }

    public void setWs_sent(long ws_sent) {
        this.ws_sent = ws_sent;
    }

    public long getWs_receive() {
        return ws_receive;
    }

    public void setWs_receive(long ws_receive) {
        this.ws_receive = ws_receive;
    }

    public long getHttp_sent() {
        return http_sent;
    }

    public void setHttp_sent(long http_sent) {
        this.http_sent = http_sent;
    }
}
