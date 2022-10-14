package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/1.0/files")
public class FileHttpController {
    private final FileService fileService;
    // TODO - interseptor that gets the File object and wraps  -   public ResponseEntity<Response<FILE DTO>> interseptor (Object o)
    // TODO - add validations - json schema validation
    @GetMapping
    public ResponseEntity<List<FileDto>> list() {
        List<FileDto> files = fileService.list();
        return ResponseEntity.status(HttpStatus.OK).
                body(files);
    }

    @PostMapping
    public ResponseEntity<FileDto> create(@RequestBody /*todo @MorErel @RequestValidator("src/main/resources/schemas/file/create.json")*/ FileDto file) {
        FileDto addedFile = fileService.create(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFile);
    }

    @GetMapping("{id}")
    public ResponseEntity<FileDto> download(@PathVariable UUID id) {
        FileDto file = fileService.findById(id)
                .orElseThrow(()-> new FileNotFoundError(id));
        return ResponseEntity.status(HttpStatus.OK)
                .body(file);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<FileDto> updateName(@PathVariable UUID id, @RequestBody FileDto file) {
        File fileDb = fileService.toDb(file);
        fileDb.setId(id);
        File updatedFile = fileService.updateName(fileDb);
        FileDto updatedDto = fileService.toDto(updatedFile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedDto);
    }
}
