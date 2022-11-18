package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.utils.http.response.ResponseWrapper;
import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import com.project.sharedfolderserver.v1.utils.JsonSchema;
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

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@ResponseWrapper
@RequestMapping(path = "/1.0/files")
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
    public ResponseEntity<FileDto> create(@Validate(JsonSchema.FILE_CREATE) FileDto fileToAdd) {
        log.info("in create, request body: " + fileToAdd);
        FileDto addedFile = fileService.create(fileToAdd);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFile);
    }

    @Operation(summary = "Download a file")
    @GetMapping("{id}")
    public ResponseEntity<FileDto> download(@PathVariable UUID id) {
        FileDto file = fileService.findById(id)
                .orElseThrow(() -> new FileNotFoundError(id));
        return ResponseEntity.status(HttpStatus.OK)
                .body(file);
    }

    @Operation(summary = "Delete a file")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("in delete, id: " + id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change file name")
    @PutMapping("{id}")
    public ResponseEntity<FileDto> updateName(@PathVariable UUID id, @Validate(JsonSchema.FILE_UPDATE) FileDto fileToUpdate) {
        fileToUpdate.setId(id);
        FileDto updatedFile = fileService.updateName(fileToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedFile);
    }
}
