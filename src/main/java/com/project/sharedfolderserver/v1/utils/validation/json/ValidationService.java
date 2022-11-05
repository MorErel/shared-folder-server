package com.project.sharedfolderserver.v1.utils.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.project.sharedfolderserver.v1.file.exception.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ValidationService {
    private final Map<String, JsonSchema> schemaCache = new HashMap<>();

    public void validate(JsonNode jsonNode, String path) {
        log.debug("validating schema: {}", path);
        if (path == null) {
            log.error("Could not resolve json schema path, null");
            throw new ValidationError("JsonSchema path is null");
        }
        JsonSchema jsonSchemaToValidate = schemaCache.computeIfAbsent(path, p -> {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            try {
                JsonSchema jsonSchema = factory.getSchema(new URI("classpath:" + path));
                return jsonSchema;
            } catch (Exception e) {
                log.error("Could not load json schema: {}", e.getMessage());
                throw new ValidationError(String.format("Could not load json schema: %s", e.getMessage()));
            }
        });
        schemaCache.putIfAbsent(path,jsonSchemaToValidate);
        Set<ValidationMessage> errors = jsonSchemaToValidate.validate(jsonNode);
        log.info("errors: " + errors);
        if (!errors.isEmpty()) {
            throw new ValidationError(errors.toString());
        }
    }
}
