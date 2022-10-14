package com.project.sharedfolderserver.v1.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.v1.file.exception.FileCannotBeCreatedError;
import com.project.sharedfolderserver.v1.file.exception.FileCannotBeDeletedError;
import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import com.project.sharedfolderserver.v1.utils.json.JsonUtil;
import com.project.sharedfolderserver.v1.utils.validation.json.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "api/v1/file")
public class FileHttpController {
    private final FileService fileService;
    private final ValidationService validationService;

    // TODO - interseptor that gets the File object and wraps  -   public ResponseEntity<Response<FILE DTO>> interseptor (Object o)
    // TODO - add validations - json schema validation
    @GetMapping
    public ResponseEntity<List<FileDto>> list() {
        List<FileDto> files = fileService.list();
        return ResponseEntity.status(HttpStatus.OK).
                body(files);
    }

    @PostMapping
    //todo -
    //@RequestValidator("src/main/resources/schemas/file/create.json")
    public ResponseEntity<FileDto> create(@RequestBody JsonNode file) {
        log.info("in create, request body: " + file);
        validationService.validate(file, "schemas/file/create.json");
        FileDto fileToAdd = JsonUtil.mapper.convertValue(file, new TypeReference<>() {
        });
        FileDto addedFile = fileService.create(fileToAdd);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFile);
    }

    // TODO - add validations - json schema validation
    @GetMapping("{id}")
    public ResponseEntity<FileDto> download(@PathVariable UUID id) {
        FileDto file = fileService.findById(id)
                .orElseThrow(() -> new FileNotFoundError(id));
        return ResponseEntity.status(HttpStatus.OK)
                .body(file);
    }

    // TODO - add validations - json schema validation
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("in delete, id: " + id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // TODO - add validations - json schema validation
    @PutMapping("{id}")
    public ResponseEntity<FileDto> updateName(@PathVariable UUID id, @RequestBody JsonNode file) {
        validationService.validate(file, "schemas/file/update.json");
        FileDto fileToUpdate = JsonUtil.mapper.convertValue(file, new TypeReference<>() {
        });
        fileToUpdate.setId(id);
        FileDto updatedFile = fileService.updateName(fileToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedFile);
    }
}
