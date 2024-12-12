package com.mudcode.cli;

import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "hardware",
        mixinStandardHelpOptions = true
)
public class HardwareInfoCommand implements Callable<Integer> {
    private static final Logger logger = LogUtil.logger(HardwareInfoCommand.class);

    @Override
    public Integer call() throws Exception {
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            String hardwareUUID = hal.getComputerSystem().getHardwareUUID();
            LogUtil.console("HardwareUUID: {}", hardwareUUID);
            String serialNumber = hal.getComputerSystem().getSerialNumber();
            LogUtil.console("SerialNumber: {}", serialNumber);
            return 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 1;
        }
    }
}
