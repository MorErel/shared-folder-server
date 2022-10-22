package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class FileCannotBeUpdatedError extends BadRequestError {
    public FileCannotBeUpdatedError() {
        super(ErrorMessages.FILE_CANNOT_BE_UPDATED);
    }

    public FileCannotBeUpdatedError(String error) {
        super(String.format(ErrorMessages.FILE_CANNOT_BE_UPDATED, error));
    }
}
