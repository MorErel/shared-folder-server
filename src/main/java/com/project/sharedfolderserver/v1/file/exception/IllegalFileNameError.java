package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class IllegalFileNameError extends BadRequestError {

    public IllegalFileNameError(String fileName) {
        super(String.format(ErrorMessages.ILLEGAL_FILE_NAME, fileName));
    }
}
