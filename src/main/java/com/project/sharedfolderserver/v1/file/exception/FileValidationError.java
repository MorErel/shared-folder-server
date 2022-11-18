package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class FileValidationError extends BadRequestError {
    public FileValidationError(String error) {
        super(String.format(ErrorMessages.REQUEST_VALIDATION_ERROR, error));
    }
}
