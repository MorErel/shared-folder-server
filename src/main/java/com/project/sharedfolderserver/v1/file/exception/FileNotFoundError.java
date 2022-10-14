package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

import java.util.UUID;

public class FileNotFoundError extends BadRequestError {

    public FileNotFoundError(UUID uuid) {
        super(String.format(ErrorMessages.FILE_NOT_FOUND, uuid));
    }
}
