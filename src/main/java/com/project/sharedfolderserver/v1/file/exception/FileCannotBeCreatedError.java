package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;

public class FileCannotBeCreatedError extends BadRequestError {
    public FileCannotBeCreatedError(String message) {
        super(message);
    }
}
