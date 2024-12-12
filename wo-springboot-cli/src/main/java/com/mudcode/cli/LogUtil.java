package com.mudcode.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger CONSOLE_LOG = LoggerFactory.getLogger("CONSOLE_LOG");

    public static Logger logger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void console(String log) {
        CONSOLE_LOG.info(log);
    }

    public static void console(String format, Object... arguments) {
        CONSOLE_LOG.info(format, arguments);
    }
}
