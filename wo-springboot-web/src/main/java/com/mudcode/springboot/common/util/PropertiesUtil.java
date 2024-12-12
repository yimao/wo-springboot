package com.mudcode.springboot.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertiesUtil {

    private static final String PROTOCOL_FILE = "file:";

    private static final String PROTOCOL_CLASSPATH = "classpath:";

    private PropertiesUtil() {
    }

    public static void load(String filePath, Properties properties) throws IOException {
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(properties);

        if (filePath.startsWith(PROTOCOL_FILE)) {
            try (InputStream inputStream = new FileInputStream(filePath.substring(PROTOCOL_FILE.length()).trim())) {
                properties.load(inputStream);
            }
        } else if (filePath.startsWith(PROTOCOL_CLASSPATH)) {
            String path = filePath.substring(PROTOCOL_CLASSPATH.length()).trim();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)) {
                properties.load(inputStream);
            }
        } else {
            throw new IllegalArgumentException("filePath must be start with 'file:' or 'classpath:'");
        }
    }

}
