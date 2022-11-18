package com.project.sharedfolderserver.v1.file;

import com.project.sharedfolderserver.v1.file.exception.FileNameAlreadyExistsError;
import com.project.sharedfolderserver.v1.file.exception.FileNameCannotBeEmpty;
import com.project.sharedfolderserver.v1.file.exception.IllegalFileNameError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RequiredArgsConstructor
public class FileUtils {
    // this regex ensures the name of the file will not consist the special characters !;.@#$%^&*(){}?<>`|=\ , then followed by 1 dot
    // after the dot - the suffix will be a legal file kind contains letters and number only
    private final static String regex = "^[^!;.@#$%^&*()\\{\\}?<>`|=\\\\]+\\.{1}[a-zA-Z0-9]+$";
    public static final String FILENAME = "filename";
    public static final String FILE_NAME_VALIDATED_SUCCESSFULLY_MESSAGE = "file name validated successfully: {}";

    /**
     * Validate file name, throws exception if not valid
     * @param name - the file name to validate
     * @param fileRepository - file repository
     * @throws FileNameCannotBeEmpty - if file name is empty
     * @throws IllegalFileNameError - if file name is illegal
     * @throws FileNameAlreadyExistsError - if file name already exists
     */
    public static void assertFileName(String name, FileRepository fileRepository) {
        log.debug("validating file name: {}",kv("filename",name));
        if (!StringUtils.hasText(name)) {
            log.error(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
            throw new FileNameCannotBeEmpty();
        }
        if (!Pattern.matches(regex, name)) {
            log.error(ErrorMessages.ILLEGAL_FILE_NAME + name);
            throw new IllegalFileNameError(name);
        }
        if(fileRepository.countByName(name)>0) {
            log.error(ErrorMessages.FILE_NAME_ALREADY_EXISTS + name);
            throw new FileNameAlreadyExistsError(name);
        }
        log.debug(FILE_NAME_VALIDATED_SUCCESSFULLY_MESSAGE,kv(FILENAME,name));
    }
}
