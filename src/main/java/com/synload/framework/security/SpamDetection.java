package com.synload.framework.security;

import net.jodah.expiringmap.ExpiringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nathaniel on 7/22/2016.
 */
public class SpamDetection {
    public static Map<String, List<Long>> access = ExpiringMap.builder().expiration(60,TimeUnit.SECONDS).build();
    public static int limit = 15;
    public static int time = 200;
    public static boolean respondAllowed(String identifier){
        if(access.containsKey(identifier)){
            long currentTime = System.currentTimeMillis() % 1000;
            int count = 0;
            List<Long> conns = new ArrayList<Long>(access.get(identifier));
            for(long t : conns){
                if(t+time>currentTime){
                    count++;
                }else{
                    access.get(identifier).remove(t);
                }
            }
            if(count<limit){
                return true;
            }else{
                return false;
            }
        }else{
            long currentTime = System.currentTimeMillis() % 1000;
            access.put(identifier, new ArrayList<Long>());
            access.get(identifier).add(currentTime);
            return true;
        }
    }
}
