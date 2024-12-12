package com.mudcode.cli;

import org.slf4j.Logger;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "system",
        mixinStandardHelpOptions = true
)
public class SystemCommand implements Callable<Integer> {
    private static final Logger logger = LogUtil.logger(SystemCommand.class);

    @Override
    public Integer call() throws Exception {
        try {
            Map<String, Object> env = new HashMap<>(System.getenv());
            Map<String, Object> props = new HashMap<>();
            System.getProperties().forEach((k, v) -> props.put(k.toString(), v));
            Map<String, Object> map = new HashMap<>();
            map.put("env", env);
            map.put("props", props);
            LogUtil.console("{}", JsonUtil.toJson(map));
            return 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 1;
        }
    }
}
