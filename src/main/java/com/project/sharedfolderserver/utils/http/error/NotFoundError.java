package com.project.sharedfolderserver.utils.http.error;

import org.springframework.http.HttpStatus;

public class NotFoundError extends BaseError {
    public NotFoundError(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
