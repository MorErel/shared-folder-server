package com.project.sharedfolderserver.v1.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.sharedfolderserver.utils.http.response.ResponseWrapper;
import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import com.project.sharedfolderserver.v1.utils.JsonSchema;
import com.project.sharedfolderserver.v1.utils.json.JsonUtil;
import com.project.sharedfolderserver.v1.utils.validation.json.Validate;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@ResponseWrapper
@RequestMapping(path = "/1.0/files")
/**
 * Http controller to serve file request
 */
public class FileHttpController {
    private final FileService fileService;

    // todo - edit all the descriptions

    @Operation(summary = "Get a list of the files")
    @GetMapping
    public ResponseEntity<List<FileDto>> list() {
        List<FileDto> files = fileService.list();
        return ResponseEntity.status(HttpStatus.OK).
                body(files);
    }

    @Operation(summary = "Upload a new file")
    @PostMapping
    public ResponseEntity<FileDto> upload(@Validate(JsonSchema.FILE_CREATE) FileDto fileToAdd) {
        log.info("saving file {}", fileToAdd.getName());
        FileDto addedFile = fileService.save(fileToAdd);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFile);
    }

    @Operation(summary = "Download a file")
    @GetMapping("{fileId}")
    public ResponseEntity<FileDto> download(@PathVariable UUID fileId) {
        log.info("downloading file: {}",kv("fileID",fileId));

        FileDto file = fileService.findById(fileId)
                .orElseThrow(() -> new FileNotFoundError(fileId));
        return ResponseEntity.status(HttpStatus.OK)
                .body(file);
    }

    @Operation(summary = "Delete a file")
    @DeleteMapping("{fileId}")
    public ResponseEntity<Void> delete(@PathVariable UUID fileId) {
        log.info("deleting file: {}",kv("fileID",fileId));
        fileService.deleteOne(fileId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change file name")
    @PutMapping("{fileId}")
    public ResponseEntity<FileDto> updateName(@PathVariable UUID fileId, @Validate(JsonSchema.FILE_UPDATE) FileDto fileToUpdate) {
        log.info("updating file: {}",kv("fileID",fileId));
        fileToUpdate.setId(fileId);
        FileDto updatedFile = fileService.updateName(fileToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedFile);
    }
}
