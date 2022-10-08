package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;

public class FileNameAlreadyExistsError extends BadRequestError {

    private final static String MESSAGE = "file with the name %s already exists";

    public FileNameAlreadyExistsError(String fileName)
    {
        super(String.format(MESSAGE, fileName));
    }
}
