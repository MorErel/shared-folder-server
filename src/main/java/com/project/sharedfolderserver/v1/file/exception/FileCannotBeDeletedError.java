package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;

public class FileCannotBeDeletedError extends BadRequestError {
    public FileCannotBeDeletedError(String message) {
        super(message);
    }
}
