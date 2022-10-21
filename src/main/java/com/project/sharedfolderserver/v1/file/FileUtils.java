package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.v1.file.exception.FileNameAlreadyExistsError;
import com.project.sharedfolderserver.v1.file.exception.FileNameCannotBeEmpty;
import com.project.sharedfolderserver.v1.file.exception.IllegalFileName;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileUtils {

    private final static String regex = "^[^!;.@#$%^&*()\\{\\}?<>`|=\\\\]+\\.{1}[a-zA-Z0-9]+$";

    public static void validateFileName(String name, FileRepository fileRepository) {
        log.info("validating file name");
        if (!StringUtils.hasText(name)) {
            log.error(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
            throw new FileNameCannotBeEmpty();
        }
        if (!Pattern.matches(regex, name)) {
            log.error(ErrorMessages.ILLEGAL_FILE_NAME);
            throw new IllegalFileName(name);
        }
        fileRepository.findByName(name)
                .ifPresent(file -> {
                    log.error(ErrorMessages.FILE_NAME_ALREADY_EXISTS);
                    throw new FileNameAlreadyExistsError(name);
                });
    }
}
