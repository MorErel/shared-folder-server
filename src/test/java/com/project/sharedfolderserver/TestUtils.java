package com.project.sharedfolderserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.v1.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TestUtils {

    public static JsonNode getTestCase(String filename) throws IOException {
        File file = new ClassPathResource(String.format("/cases/%s.json", filename)).getFile();
        return JsonUtil.objectMapper.readTree(file);
    }

    public static <T> void assertEqualsExcludedFields(T expectedData, T actualData, String... excludedFields) throws IllegalAccessException {
        Set<String> excludedFieldsHash = Stream.of(excludedFields).collect(Collectors.toSet());
        if (expectedData == null || actualData == null) {
            log.error("expectedData or actualData is null");
            throw new RuntimeException("expectedData or actualData is null");
        }
        Field[] objectFields = expectedData.getClass().getDeclaredFields();
        for (Field field : objectFields) {
            String currentField = field.getName();
            if (excludedFieldsHash.contains(currentField)) {
                log.info("skipping excluded field: {}", currentField);
                continue;
            }
            field.setAccessible(true);
            Object expectedField = field.get(expectedData);
            Object actualField = field.get(actualData);
            log.info("comparing {} to {}", expectedField, actualField);
            if (!Objects.equals(actualField,expectedField)) {
                log.error("field {} is not equal, expected: {}, actual: {}", currentField, expectedField, actualField);
                throw new RuntimeException(String.format("field %s is not equal, expected: %s, actual: %s", currentField, expectedField, actualField));
            }
        }
    }
}
