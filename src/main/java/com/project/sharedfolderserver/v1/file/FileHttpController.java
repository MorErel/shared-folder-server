package com.project.sharedfolderserver.v1.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import com.project.sharedfolderserver.v1.utils.JsonSchema;
import com.project.sharedfolderserver.v1.utils.json.JsonUtil;
import com.project.sharedfolderserver.v1.utils.validation.json.Validate;
import com.project.sharedfolderserver.v1.utils.validation.json.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/1.0/files")
public class FileHttpController {
    private final FileService fileService;

    // TODO - if there's time - interseptor that gets the File object and wraps  -   public ResponseEntity<Response<FILE DTO>> interseptor (Object o)
    @GetMapping
    public ResponseEntity<List<FileDto>> list() {
        List<FileDto> files = fileService.list();
        return ResponseEntity.status(HttpStatus.OK).
                body(files);
    }

    @PostMapping
    public ResponseEntity<FileDto> create(@RequestBody @Validate(JsonSchema.FILE_CREATE) FileDto fileToAdd) {
        log.info("in create, request body: " + fileToAdd);
        FileDto addedFile = fileService.create(fileToAdd);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFile);
    }

    @GetMapping("{id}")
    public ResponseEntity<FileDto> download(@PathVariable UUID id) {
        FileDto file = fileService.findById(id)
                .orElseThrow(() -> new FileNotFoundError(id));
        return ResponseEntity.status(HttpStatus.OK)
                .body(file);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("in delete, id: " + id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<FileDto> updateName(@PathVariable UUID id, @RequestBody @Validate(JsonSchema.FILE_UPDATE) FileDto fileToUpdate) {
        fileToUpdate.setId(id);
        FileDto updatedFile = fileService.updateName(fileToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedFile);
    }
}
