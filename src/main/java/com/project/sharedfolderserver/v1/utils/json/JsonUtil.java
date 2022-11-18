package com.project.sharedfolderserver.v1.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Json util class
 */
public class JsonUtil {
    public final static ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    public static ObjectNode object() {
        return objectMapper.createObjectNode();
    }

}
