package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.v1.file.exception.FileCannotBeCreatedError;
import com.project.sharedfolderserver.v1.file.exception.FileNameAlreadyExistsError;
import com.project.sharedfolderserver.v1.file.exception.FileNameCannotBeEmpty;
import com.project.sharedfolderserver.v1.file.exception.IllegalFileName;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileUtils {

    public static LocalDateTime getTimeStamp()
    {
        return LocalDate.now().atTime(LocalTime.now());
    }
    public static void validateFileName(String name, FileRepository fileRepository)
    {
        log.info("validating file name");
        if (!StringUtils.hasText(name)) {
            log.error(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
            throw new FileNameCannotBeEmpty(name);
        }
        if(!name.matches("^[a-zA-Z0-9_\\-]+\\.[a-zA-Z0-9_\\-]+$"))
        {
            log.error(ErrorMessages.ILLEGAL_FILE_NAME);
            throw new IllegalFileName(name);
        }
        fileRepository.findByName(name)
                .ifPresent(file -> {
                    log.error(ErrorMessages.FILE_NAME_ALREADY_EXISTS);
                    throw new FileNameAlreadyExistsError(name);});
    }
}
