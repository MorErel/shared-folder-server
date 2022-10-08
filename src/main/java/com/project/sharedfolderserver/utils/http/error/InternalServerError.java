package com.project.sharedfolderserver.utils.http.error;

import org.springframework.http.HttpStatus;

public class InternalServerError extends BasicError{
    public InternalServerError(String message)
    {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
