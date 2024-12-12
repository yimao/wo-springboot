package com.mudcode.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    private static final Logger access = LoggerFactory.getLogger("access");

    public static Logger logger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void access(String accessLog) {
        if (access.isTraceEnabled()) {
            access.trace(accessLog);
        }
    }

    public static void access(String format, Object... arguments) {
        if (access.isTraceEnabled()) {
            access.trace(format, arguments);
        }
    }

}
