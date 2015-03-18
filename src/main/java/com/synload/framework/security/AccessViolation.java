package com.synload.framework.security;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.SynloadFramework;

public class AccessViolation {
    public static void accessViolation(String ipAddress) {
        long millis = System.currentTimeMillis();
        if (!ipAddress.equals("")) {
            if (SynloadFramework.failedAttempts.containsKey(ipAddress)) {
                int counts = 0;
                List<Long> attempts = SynloadFramework.failedAttempts
                        .get(ipAddress);
                for (long attempt : attempts) {
                    if (millis < 5000 + attempt) {
                        counts++;
                    }
                }
                if (counts >= SynloadFramework.totalFailures) {
                    SynloadFramework.bannedIPs.add(ipAddress);
                } else {
                    attempts.add(millis);
                }
            } else {
                List<Long> attempts = new ArrayList<Long>();
                attempts.add(millis);
                SynloadFramework.failedAttempts.put(ipAddress, attempts);
            }
        }
    }
}
