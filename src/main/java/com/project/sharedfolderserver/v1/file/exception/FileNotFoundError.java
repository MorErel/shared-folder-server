package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;

import java.util.UUID;

public class FileNotFoundError extends BadRequestError {
    private final static String MESSAGE = "file with the id %s does not exist";
    public FileNotFoundError(UUID uuid)
    {
        super(String.format(MESSAGE, uuid));
    }
}
