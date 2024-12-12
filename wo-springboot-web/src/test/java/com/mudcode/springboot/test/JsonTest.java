package com.mudcode.springboot.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.Gson;
import com.mudcode.springboot.bean.IdNameItem;
import com.mudcode.springboot.common.util.HttpClientUtil;
import com.mudcode.springboot.common.util.JsonUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonTest.class);

    private JsonMapper jsonMapper;

    private IdNameItem item;

    @BeforeEach
    public void before() {
        jsonMapper = new JsonMapper();
        item = new IdNameItem();
        item.setId(1);
        item.setName("a");
        item.setDateTime(new Date());
        item.setEnabled(true);
        item.setTags(new String[]{"t1", "t2"});
        System.out.println(item);
        System.out.println(JsonUtil.toJson(item));
    }

    @Test
    public void testToJson() throws JsonProcessingException {
        System.out.println(jsonMapper.writeValueAsString(item));

        Gson gson = new Gson();
        System.out.println(gson.toJson(item));
    }

    @Test
    public void testJsonObject() throws JsonProcessingException {
        String json = "[1,2,3,\"a\",\"b\",\"c\"]";
        List<Object> list = JsonUtil.toObjectList(json);
        System.out.println(list);
        String mapJson = "{\"a1\":{\"a11\":11,\"a12\":12},\"b1\":{\"b11\":\"v11\",\"b12\":\"v12\"}}";
        Map<String, Object> map = JsonUtil.toObjectMap(mapJson);
        System.out.println(map);

        JsonNode rootNode = this.jsonMapper.readTree(json);
        StringBuilder builder = new StringBuilder();
        if (rootNode.isArray()) {
            rootNode.forEach(item -> System.out.println(item.asText()));
            System.out.println(String.join(",", jsonMapper.treeToValue(rootNode, String[].class)));
        }
    }

    @Test
    public void testJsonEachRow() throws IOException {
        List<IdNameItem> list = new ArrayList<>();
        list.add(item);

        IdNameItem item2 = new IdNameItem();
        item2.setId(1);
        item2.setName("a");
        list.add(item2);

        StringWriter stringWriter = new StringWriter();
        SequenceWriter sequenceWriter = jsonMapper.writer().withRootValueSeparator("\n").writeValues(stringWriter);
        sequenceWriter.writeAll(list);
        System.out.println(stringWriter.toString());

        String jsonEachRow = """
                {"Timestamp":"2024-01-29 00:08:59.668297000","TraceId":"9272e92582685d1a80d3a8b998d18aad","SpanId":"acb31ede7fda71fb","SpanName":"AUTH","ServiceName":"common-api-gateway","Duration":"923779"}
                {"Timestamp":"2024-01-29 00:09:01.086094000","TraceId":"900d293e4e6f70f7a85aa8cce1177560","SpanId":"aa2667305cc0b3e2","SpanName":"AUTH","ServiceName":"common-api-gateway","Duration":"1059279"}
                """;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonEachRow));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            Map<String, Object> data = jsonMapper.readValue(line, new TypeReference<Map<String, Object>>() {
            });
            System.out.println(data);
        }
    }

    @Test
    public void testGithubPull() throws URISyntaxException, IOException {
        URL file = this.getClass().getResource("/json/pulls.json");
        byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));

        JsonNode root = jsonMapper.readTree(bytes);
        StringBuilder builder = new StringBuilder();

        try (CSVPrinter printer = new CSVPrinter(new FileWriter("./target/pulls.csv"), CSVFormat.EXCEL)) {
            root.forEach(jsonNode -> {
                String title = jsonNode.findPath("title").asText();
                String closed_at = jsonNode.findPath("closed_at").asText();
                String action = jsonNode.findPath("merged_at").isNull() ? "cancel" : "merged";
                builder.append("\n").append(title).append("\t").append(closed_at).append("\t").append(action);
                try {
                    printer.printRecord(title, closed_at, action);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        }

        logger.info(builder.toString());
    }

    @Test
    public void testCVE() throws URISyntaxException, IOException {
        URL file = this.getClass().getResource("/json/02.json");
        byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));

        JsonNode root = jsonMapper.readTree(bytes);
        JsonNode cveItems = root.findPath("data").findPath("data");
        logger.info("0");
        cveItems.forEach(item -> {
            logger.info(item.findPath("path").asText());
        });
        logger.info("1");
    }

    @Test
    public void dependencyCheck() throws Exception {
        String url = "http://releasego.tingyun.com/api/artifact/%s/%s/%s";

        String groupId = "org.reflections";
        String artifactId = "reflections";
        String versionId = "0.9.12";
        versionId = "";

        Map<String, String> header = new HashMap<>();
        // "releasego:Wvi4yZxUrYNcaX7r"
        header.put("Authorization", "Basic cmVsZWFzZWdvOld2aTR5WnhVcllOY2FYN3I=");

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        httpClientUtil.init();
        String json = httpClientUtil.get(String.format(url, groupId, artifactId, versionId), header, null);
        httpClientUtil.close();

        JsonNode root = jsonMapper.readTree(json);
        JsonNode dataNode = root.findPath("data");

        CSVFormat csvFormat = CSVFormat.Builder.create(CSVFormat.EXCEL)
                .setHeader("group_id", "artifact_id", "version", "product", "app_group_id", "app_artifact_id",
                        "app_version")
                .build();
        try (CSVPrinter printer = new CSVPrinter(new FileWriter("./target/dependencies.csv"), csvFormat)) {
            dataNode.forEach(data -> data.findPath("relatedApplications").forEach(app -> {
                String group = data.findPath("group").asText();
                String artifact = data.findPath("artifact").asText();
                String version = data.findPath("version").asText();

                String product = app.findPath("product").asText();
                String appGroup = app.findPath("group").asText();
                String appArtifact = app.findPath("artifact").asText();
                String appVer = app.findPath("version").asText();

                try {
                    printer.printRecord(group, artifact, version, product, appGroup, appArtifact, appVer);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }));
        }
    }

    @Test
    public void testDruidCompactTmplString() throws IOException {
        URL url = this.getClass().getResource("/json/compact-tmpl.json");
        if (url != null) {
            System.out.println(Files.readString(Paths.get(url.getPath())));
        }
    }

}
