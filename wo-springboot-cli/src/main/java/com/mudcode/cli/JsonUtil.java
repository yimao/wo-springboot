package com.mudcode.cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static final JsonMapper jsonMapper = new JsonMapper();

    private static final ListTypeReference listTypeReference = new ListTypeReference();

    private static final MapTypeReference mapTypeReference = new MapTypeReference();

    static {
        jsonMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    private JsonUtil() {
    }

    public static <T> T toObject(String json, Class<T> elementClass) {
        try {
            return jsonMapper.readValue(json, elementClass);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        try {
            return jsonMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T toObject(byte[] json, Class<T> elementClass) {
        try {
            return jsonMapper.readValue(json, elementClass);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T toObject(byte[] json, TypeReference<T> typeReference) {
        try {
            return jsonMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T toObject(JsonNode node, Class<T> elementClass) {
        try {
            return jsonMapper.treeToValue(node, elementClass);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T toObject(JsonNode node, TypeReference<T> typeReference) {
        try {
            return jsonMapper.treeToValue(node, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Object> toObjectList(String json) {
        try {
            return jsonMapper.readValue(json, listTypeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Map<String, Object> toObjectMap(String json) {
        try {
            return jsonMapper.readValue(json, mapTypeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return jsonMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toJson(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toJsonPretty(Object object) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] toJsonBytes(Object object) {
        try {
            return jsonMapper.writeValueAsBytes(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static class MapTypeReference extends TypeReference<Map<String, Object>> {
    }

    private static class ListTypeReference extends TypeReference<List<Object>> {
    }
}
