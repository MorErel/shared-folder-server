package com.project.sharedfolderserver.utils.http.error;

import org.springframework.http.HttpStatus;

public class NotFoundError extends BasicError{
    public NotFoundError(String message)
    {
        super(message, HttpStatus.NOT_FOUND);
    }
}
