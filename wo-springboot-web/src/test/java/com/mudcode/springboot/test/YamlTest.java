package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

public class YamlTest {
    @Test
    public void test() throws IOException {
        Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));
        Map<String, Object> data = null;
        try (InputStream stream = this.getClass().getResourceAsStream("/application-0.yml")) {
            data = yaml.load(stream);
        }

        // 转换为 Properties 格式
        Properties properties = new Properties();
        flattenMap("", data, properties);
        System.out.println(properties);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // 使用块格式
        options.setIndent(4); // 缩进设置为 4
        options.setPrettyFlow(true); // 美化输出
        Yaml formattedYaml = new Yaml(new Representer(options), options);

        formattedYaml.dump(properties, new OutputStreamWriter(System.out));
    }

    private static void flattenMap(String prefix, Map<String, Object> map, Properties properties) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 递归处理嵌套 Map
                flattenMap(key, (Map<String, Object>) value, properties);
            } else {
                // 直接存储键值对
                properties.put(key, value.toString());
            }
        }
    }
}
