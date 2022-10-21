package com.project.sharedfolderserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.v1.utils.json.JSON;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class TestUtils {

    public static JsonNode getTestCase(String filename) throws IOException {
        File file = new ClassPathResource(String.format("/cases/%s.json", filename)).getFile();
        return JSON.objectMapper.readTree(file);
    }

}
