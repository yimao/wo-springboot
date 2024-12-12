package com.mudcode.springboot.common;

import java.lang.management.ManagementFactory;

/**
 * @see org.springframework.boot.system.ApplicationPid
 * @see org.springframework.boot.context.ApplicationPidFileWriter
 */
public class ApplicationPid {

    private ApplicationPid() {
    }

    public static String getPid() {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        } catch (Throwable ex) {
            return null;
        }
    }

}
