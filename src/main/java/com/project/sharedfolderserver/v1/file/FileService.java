package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.v1.file.exception.*;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import com.project.sharedfolderserver.v1.utils.model.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.project.sharedfolderserver.v1.utils.error.ErrorMessages.FILE_CONTENT_CANT_BE_CHANGED;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    public static final String FILE_NAME = "fileName";
    public static final String FILE_ID = "fileId";
    private final FileRepository fileRepository;

    /**
     * Get file list
     * @return - list of FileDto
     */
    public List<FileDto> list() {
        log.info("listing files");
        return fileRepository.findAllWithoutContent();
    }

    /**
     * Save new file
     * @param fileToSave - file to save
     * @return - the saved FileDto
     */
    public FileDto save(FileDto fileToSave) {
        log.info("saving file");
        if (Objects.isNull(fileToSave)) {
            log.error(ErrorMessages.FILE_IS_NULL_ERROR_MESSAGE);
            throw new FileCannotBeCreatedError(ErrorMessages.FILE_IS_NULL_ERROR_MESSAGE);
        }
        String requestedFilename = fileToSave.getName();
        log.info("requested file name: {}", kv(FILE_NAME,requestedFilename));
        try {
            FileUtils.assertFileName(requestedFilename, fileRepository);

            File fileBeforeSaving = toDb(fileToSave);
            fileBeforeSaving.setSize();
            fileBeforeSaving.setKind();
            fileBeforeSaving.setDateModified(Instant.now());
            fileBeforeSaving.setDateAdded(Instant.now());
            File savedFile = fileRepository.save(fileBeforeSaving);
            UUID savedFileId = savedFile.getId();
            log.debug("saved file with id {}", kv(FILE_ID,savedFileId));
            savedFile.setContent(null);
            return toDto(savedFile);
        } catch (Exception e) {
            log.error(ErrorMessages.FILE_CANNOT_BE_CREATED_ERROR_MESSAGE + e.getMessage());
            throw new FileCannotBeCreatedError(ErrorMessages.FILE_CANNOT_BE_CREATED_ERROR_MESSAGE + e.getMessage());
        }
    }

    /**
     * Delete a given file
     * @param fileId - file id to delete
     * @throws FileCannotBeDeletedError - if file cannot be deleted
     */
    public void deleteOne(UUID fileId) {
        log.info("deleting file: {} ",kv(FILE_ID,fileId));
        findById(fileId)
                .orElseThrow(() -> new FileNotFoundError(fileId));
        try {
            fileRepository.deleteById(fileId);
        } catch (Exception e) {
            log.error("Could not delete file {}: {}",kv(FILE_ID,fileId), e.getMessage());
            throw new FileCannotBeDeletedError(e.getMessage());
        }
        log.info("file {} successfully deleted",kv(FILE_ID,fileId));
    }

    /**
     * Update file name
     * @param file - the file to update
     * @return - the updated fileDto
     * @throws FileCannotBeUpdatedError - in case the file cannot be updated
     */
    public FileDto updateName(FileDto file) {
        if (file == null) {
            log.warn("file is null");
            return null;
        }
        log.info("updating file name for {}",kv(FILE_ID, file.getId()));
        UUID id = file.getId();
        FileDto fileDto = findById(id).orElseThrow(() -> new FileNotFoundError(id));
        String newName = file.getName();
        if (file.getContent() != null) {
            log.error(FILE_CONTENT_CANT_BE_CHANGED);
            throw new FileCannotBeUpdatedError(FILE_CONTENT_CANT_BE_CHANGED);
        }
        try {
            log.info("validating new name " + newName);
            FileUtils.assertFileName(newName, fileRepository);
            log.info("name validated successfully");

            File fileBeforeSaving = toDb(fileDto);
            fileBeforeSaving.setDateModified(Instant.now());
            fileBeforeSaving.setDateAdded(fileDto.getDateAdded());
            fileBeforeSaving.setName(newName);
            fileBeforeSaving.setKind();
            File savedFile = fileRepository.save(fileBeforeSaving);
            UUID savedID = savedFile.getId();
            log.debug("updated file with id " + savedID);
            savedFile.setContent(null);
            FileDto withoutContent = toDto(savedFile);
            log.debug("fileDtoWithoutContent: " + withoutContent);

            return withoutContent;
        } catch (Exception e) {
            log.error(ErrorMessages.FILE_CANNOT_BE_UPDATED + " {}", e.getMessage());
            throw new FileCannotBeUpdatedError(e.getMessage());
        }
    }

    /**
     * Find file by id
     * @param fileId - the file id
     * @return - the fileDto
     */
    public Optional<FileDto> findById(UUID fileId) {
        log.trace("findById");
        return fileRepository.findById(fileId).map(this::toDto);
    }

    /**
     * Helper method to convert from File entity to File dto
     * @param file - file entity object
     * @return - file Dto object
     */
    public FileDto toDto(File file) {
        log.trace("to Dto");
        return EntityUtil.convert(file, FileDto.class);
    }

    /**
     * Helper method to convert from File Dto to File entity
     * @param fileDto - file dto object
     * @return - file entity object
     */
    public File toDb(FileDto fileDto) {
        log.trace("to db");
        return EntityUtil.convert(fileDto, File.class);
    }
}
