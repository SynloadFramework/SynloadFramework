package com.synload.framework.security;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.synload.framework.SynloadFramework;

public class AccessViolation {
    private static final long BAN_WINDOW_MILLIS = 5000;

    public static void accessViolation(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return;
        }
        long millis = System.currentTimeMillis();

        // Use computeIfAbsent for atomic insert of new entry
        List<Long> attempts = SynloadFramework.failedAttempts.computeIfAbsent(
                ipAddress, k -> new CopyOnWriteArrayList<Long>());

        // Clean up expired attempts and count recent ones
        int counts = 0;
        Iterator<Long> it = attempts.iterator();
        while (it.hasNext()) {
            long attempt = it.next();
            if (millis >= BAN_WINDOW_MILLIS + attempt) {
                attempts.remove(attempt);
            } else {
                counts++;
            }
        }

        if (counts >= SynloadFramework.totalFailures) {
            SynloadFramework.bannedIPs.add(ipAddress);
            // Remove tracked attempts for banned IP to free memory
            SynloadFramework.failedAttempts.remove(ipAddress);
        } else {
            attempts.add(millis);
        }
    }
}
