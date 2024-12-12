package com.mudcode.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "wo-cli",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                HardwareInfoCommand.class,
                SystemCommand.class
        }
)
public class MainCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        LogUtil.console("Usage: java -jar wo-cli-bin.jar --help");
        return 1;
    }
}
