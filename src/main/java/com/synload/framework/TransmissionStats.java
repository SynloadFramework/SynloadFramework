package com.synload.framework;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Nathaniel on 7/31/2016.
 */
public class TransmissionStats {
    public static AtomicLong ws_sent = new AtomicLong(0);
    public static AtomicLong ws_receive = new AtomicLong(0);
    public static AtomicLong http_sent = new AtomicLong(0);
    public static AtomicLong http_receive = new AtomicLong(0);
}
