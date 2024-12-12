package com.mudcode.cli;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest
class CommandLineApplicationTests {
    @Autowired
    private CommandLine.IFactory factory;
    @Autowired
    private MainCommand mainCommand;

    @Test
    void hardware() {
        int exitCode = new CommandLine(mainCommand, factory).execute(
                "hardware"
        );
        assertEquals(0, exitCode);
    }

    @Test
    void system() {
        int exitCode = new CommandLine(mainCommand, factory).execute(
                "system"
        );
        assertEquals(0, exitCode);
    }
}
