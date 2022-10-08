package com.project.sharedfolderserver.v1.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class FileDto {
    private UUID id;
    private String name;
    private LocalDateTime dateModified;
    private LocalDateTime dateAdded;
    private String kind;
    private String size;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private byte[] content;

    public FileDto(UUID id, String name, LocalDateTime dateModified, LocalDateTime dateAdded, String kind, String size)
    {
        this.id = id;
        this.name = name;
        this.dateModified = dateModified;
        this.dateAdded = dateAdded;
        this.kind = kind;
        this.size = size;
    }

    public UUID getId() {
        return id;
    }
}
