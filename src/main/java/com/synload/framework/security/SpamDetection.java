package com.synload.framework.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Nathaniel on 7/22/2016.
 */
public class SpamDetection {
    public static Map<String, List<Long>> access = new ConcurrentHashMap<>();
    public static int limit = 15;
    public static int time = 200;
    public static boolean respondAllowed(String identifier){
        if(access.containsKey(identifier)){
            long currentTime = System.currentTimeMillis();
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
                access.get(identifier).add(currentTime);
                return true;
            }else{
                return false;
            }
        }else{
            long currentTime = System.currentTimeMillis();
            access.put(identifier, Collections.synchronizedList(new ArrayList<Long>()));
            access.get(identifier).add(currentTime);
            return true;
        }
    }
}
