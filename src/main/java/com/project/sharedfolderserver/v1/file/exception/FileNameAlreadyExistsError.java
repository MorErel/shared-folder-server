package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class FileNameAlreadyExistsError extends BadRequestError {

    public FileNameAlreadyExistsError(String fileName) {
        super(String.format(ErrorMessages.FILE_NAME_ALREADY_EXISTS, fileName));
    }
}
