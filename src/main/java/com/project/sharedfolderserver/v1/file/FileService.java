package com.project.sharedfolderserver.v1.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sharedfolderserver.v1.file.exception.FileCannotBeCreatedError;
import com.project.sharedfolderserver.v1.file.exception.FileCannotBeUpdatedError;
import com.project.sharedfolderserver.v1.file.exception.FileNameAlreadyExistsError;
import com.project.sharedfolderserver.v1.file.exception.FileNotFoundError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import com.project.sharedfolderserver.v1.utils.model.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
// TODO controller advise,

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
        if (!StringUtils.hasText(requestedFileName)) {
            log.error(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
            throw new FileCannotBeCreatedError(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
        }
        if (!requestedFileName.matches("^[a-zA-Z0-9_\\-]+\\.[a-zA-Z0-9_\\-]+$")) {
            log.error(ErrorMessages.ILLEGAL_FILE_NAME);
            throw new FileCannotBeCreatedError(ErrorMessages.ILLEGAL_FILE_NAME);
        }
        fileRepository.findByName(requestedFileName)
                .ifPresent(file -> {
                    throw new FileNameAlreadyExistsError(requestedFileName);
                });

        try {
            log.info("saving file: " + fileToSave);
            File fileBeforeSaving = toDb(fileToSave);
            fileBeforeSaving.setSize();
            fileBeforeSaving.setKind();
            fileBeforeSaving.setDateModified(FileUtils.getTimeStamp());
            fileBeforeSaving.setDateAdded(FileUtils.getTimeStamp());
            File savedFile = fileRepository.save(fileBeforeSaving);
            UUID savedID = savedFile.getId();
            log.info("saved file with id " + savedID);
            FileDto withoutContent = findByIdWithoutContent(savedID).orElseThrow(() -> new FileCannotBeCreatedError("could not retrieve file without content after saving"));
            log.info("fileDtoWithoutContent: " + withoutContent);
            return withoutContent;
        } catch (Exception e) {
            log.error(ErrorMessages.FILE_CANNOT_BE_CREATED_ERROR_MESSAGE + " {}", e.getMessage());
            throw new FileCannotBeCreatedError(ErrorMessages.FILE_CANNOT_BE_CREATED_ERROR_MESSAGE + e.getMessage());
        }
    }

    public void delete(UUID id) {
        log.info("deleting file with id " + id);
        findById(id);
        fileRepository.deleteById(id);
    }

    public File updateName(File file) {
        log.info("updating file name");
        UUID id = file.getId();
        FileDto fileDto = findById(id).orElseThrow(() -> new FileNotFoundError(id));
        if (file.getContent() != null)
            throw new FileCannotBeUpdatedError();

        try {
            log.info("validating new name " + file.getName());
            FileUtils.validateFileName(file.getName(), fileRepository);
            log.info("name validated successfully");
            file.setDateAdded(fileDto.getDateAdded());
            file.setKind(fileDto.getKind());
            file.setSize(fileDto.getSize());
            file.setContent(fileDto.getContent());
            file.setDateModified(FileUtils.getTimeStamp());
            return fileRepository.save(file);
        } catch (FileNameAlreadyExistsError e) {
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
