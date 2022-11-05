package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import com.project.sharedfolderserver.v1.utils.JsonSchema;
import com.project.sharedfolderserver.v1.utils.validation.json.Validate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/1.0/files")
public class FileHttpController {
    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDto>> list() {
        List<FileDto> files = fileService.list();
        return ResponseEntity.status(HttpStatus.OK).
                body(files);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<FileDto> create (@RequestParam("file") MultipartFile file) throws IOException {
        FileDto uploadedFile = new FileDto();
        uploadedFile.setName(file.getOriginalFilename());
        uploadedFile.setContent(file.getBytes());
        log.info("in create, request body: " + uploadedFile);
        FileDto addedFile = fileService.create(uploadedFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFile);
    }

    @GetMapping("{id}")
    public StreamingResponseBody download(@PathVariable UUID id) {
        FileDto file = fileService.findById(id)
                .orElseThrow(() -> new FileNotFoundError(id));
        return outputStream -> outputStream.write(file.getContent());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("in delete, id: " + id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<FileDto> updateName(@PathVariable UUID id, @Validate(JsonSchema.FILE_UPDATE) FileDto fileToUpdate) {
        fileToUpdate.setId(id);
        FileDto updatedFile = fileService.updateName(fileToUpdate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedFile);
    }
}
