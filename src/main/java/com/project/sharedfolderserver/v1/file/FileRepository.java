package com.project.sharedfolderserver.v1.file;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findByName(String name);

    @Query(value = "SELECT new com.project.sharedfolderserver.v1.file.FileDto(id, name, dateModified, dateAdded, kind, size) FROM File file WHERE file.id=id")
    Optional <FileDto> findByIdWithoutContent(UUID id);

    @Query(value = "SELECT new com.project.sharedfolderserver.v1.file.FileDto(id, name, dateModified, dateAdded, kind, size) FROM File file")
    List<FileDto> findAllWithoutContent();
}
