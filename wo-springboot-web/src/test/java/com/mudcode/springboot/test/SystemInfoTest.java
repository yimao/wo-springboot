package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class SystemInfoTest {

    private static final Logger logger = LoggerFactory.getLogger(SystemInfoTest.class);

    @Test
    public void testDefaultProps() {
        System.getenv().forEach((k, v) -> System.out.println(k + " => " + v));
        System.getProperties().forEach((k, v) -> System.out.println(k + " => " + v));
    }

    @Test
    public void testSystemInfo() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            String hardwareUUID = hal.getComputerSystem().getHardwareUUID();
            String serialNumber = hal.getComputerSystem().getSerialNumber();
            logger.debug("hardwareUUID: {}, serialNumber: {}", hardwareUUID, serialNumber);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}
