package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.file.exception.*;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import com.project.sharedfolderserver.v1.utils.model.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final EntityUtil entityUtil;

    public List<FileDto> list() {
        log.info("listing files");
        return fileRepository.findAllWithoutContent();
    }

    public FileDto create(FileDto fileToSave) {
        log.info("creating file");
        if (Objects.isNull(fileToSave)) {
            log.error(ErrorMessages.FILE_IS_NULL_ERROR_MESSAGE);
            throw new FileCannotBeCreatedError(ErrorMessages.FILE_IS_NULL_ERROR_MESSAGE);
        }
        String requestedFileName = fileToSave.getName();
        log.info("requestedFileName:" + requestedFileName);

        try {
            FileUtils.validateFileName(requestedFileName, fileRepository);
            log.info("saving file: " + fileToSave);
            File fileBeforeSaving = toDb(fileToSave);
            fileBeforeSaving.setSize();
            fileBeforeSaving.setKind();
            fileBeforeSaving.setDateModified(Instant.now());
            fileBeforeSaving.setDateAdded(Instant.now());
            File savedFile = fileRepository.save(fileBeforeSaving);
            UUID savedID = savedFile.getId();
            log.debug("saved file with id " + savedID);
            FileDto withoutContent = findByIdWithoutContent(savedID).orElseThrow(() -> new FileCannotBeCreatedError("could not retrieve file without content after saving"));
            log.debug("fileDtoWithoutContent: " + withoutContent);
            return withoutContent;
        } catch (Exception e) {
            log.error(ErrorMessages.FILE_CANNOT_BE_CREATED_ERROR_MESSAGE + " {}", e.getMessage());
            throw new FileCannotBeCreatedError(ErrorMessages.FILE_CANNOT_BE_CREATED_ERROR_MESSAGE + e.getMessage());
        }
    }

    public void delete(UUID id) {
        log.info("deleting file with id " + id);
        findById(id)
                .orElseThrow(() -> new FileNotFoundError(id));
        try {
            fileRepository.deleteById(id);
        } catch (Exception e) {
            throw new FileCannotBeDeletedError(e.getMessage());
        }
    }

    public FileDto updateName(FileDto file) {
        log.info("updating file name");
        UUID id = file.getId();
        FileDto fileDto = findById(id).orElseThrow(() -> new FileNotFoundError(id));
        String newName = file.getName();
        if (file.getContent() != null)
            throw new FileCannotBeUpdatedError();

        try {
            log.info("validating new name " + newName);
            FileUtils.validateFileName(newName, fileRepository);
            log.info("name validated successfully");

            File fileBeforeSaving = toDb(fileDto);
            fileBeforeSaving.setDateModified(Instant.now());
            fileBeforeSaving.setDateAdded(fileDto.getDateAdded());
            fileBeforeSaving.setName(newName);
            File savedFile = fileRepository.save(fileBeforeSaving);
            UUID savedID = savedFile.getId();
            log.debug("updated file with id " + savedID);
            FileDto withoutContent = findByIdWithoutContent(savedID).orElseThrow(() -> new FileCannotBeUpdatedError("could not retrieve file without content after saving"));
            log.debug("fileDtoWithoutContent: " + withoutContent);

            return withoutContent;
        } catch (Exception e) {
            log.error(ErrorMessages.FILE_CANNOT_BE_UPDATED + " {}", e.getMessage());
            throw new FileCannotBeUpdatedError(e.getMessage());
        }
    }

    public Optional<FileDto> findById(UUID id) {
        log.info("findById");
        return fileRepository.findById(id).map(this::toDto);
    }

    public Optional<FileDto> findByIdWithoutContent(UUID id) {
        return fileRepository.findByIdWithoutContent(id);
    }

    public FileDto toDto(File file) {
        log.info("to Dto");
        return (FileDto) entityUtil.convert(file, FileDto.class);
    }

    public File toDb(FileDto fileDto) {
        log.info("to db");
        return (File) entityUtil.convert(fileDto, File.class);
    }
}
