package com.synload.framework;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log {

    public static void setLevel(Level level) {
        Logger.getRootLogger().setLevel(level);
    }

    public static <T> void info(String data, Class<T> c) {
        Logger logger = Logger.getLogger(c.getName());
        logger.info(data);
    }

    public static <T> void debug(String data, Class<T> c) {
        Logger logger = Logger.getLogger(c.getName());
        logger.debug(data);
    }

    public static <T> void error(String data, Class<T> c) {
        Logger logger = Logger.getLogger(c.getName());
        logger.error(data);
    }

    public static <T> void error(String data, Class<T> c, Throwable t) {
        Logger logger = Logger.getLogger(c.getName());
        logger.setLevel(SynloadFramework.loglevel);
        logger.error(data, t);
    }
}
