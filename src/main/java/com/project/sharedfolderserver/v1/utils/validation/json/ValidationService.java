package com.project.sharedfolderserver.v1.utils.validation.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.project.sharedfolderserver.v1.file.exception.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@Slf4j
@Service
public class ValidationService {
    public void validate(JsonNode jsonNode, String path) {
        log.info("validating schema: ");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = null;
        try {
            jsonSchema = factory.getSchema(new URI("classpath:" + path));
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
        log.info("errors: " + errors);
        if (!errors.isEmpty()) {
            throw new ValidationError(errors.toString());
        }
    }
}
