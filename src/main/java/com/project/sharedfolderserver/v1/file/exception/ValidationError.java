package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class ValidationError extends BadRequestError {
    public ValidationError(String error) {
        super(String.format(ErrorMessages.REQUEST_VALIDATION_ERROR, error));
    }
}
