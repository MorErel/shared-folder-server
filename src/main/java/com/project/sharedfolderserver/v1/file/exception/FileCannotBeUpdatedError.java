package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;

public class FileCannotBeUpdatedError extends BadRequestError {
    public FileCannotBeUpdatedError(String message) {
        super(message);
    }
}
