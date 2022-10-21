package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class FileCannotBeDeletedError extends BadRequestError {
    public FileCannotBeDeletedError(String message) {
        super(String.format(ErrorMessages.FILE_CANNOT_BE_DELETED_ERROR_MESSAGE, message));
    }
}
