package com.synload.talksystem.statistics;

import com.synload.framework.SynloadFramework;
import com.synload.talksystem.ConnectionDocument;

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
    public StatisticDocument(UUID chain) {
        super(null, chain);
        free = Runtime.getRuntime().freeMemory();
        total = Runtime.getRuntime().totalMemory();
        max = Runtime.getRuntime().maxMemory();
        clients = SynloadFramework.clients.size();
        identifier = SynloadFramework.identifier;
    }
}
