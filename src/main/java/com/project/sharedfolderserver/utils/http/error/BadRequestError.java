package com.project.sharedfolderserver.utils.http.error;

import org.springframework.http.HttpStatus;

public class BadRequestError extends BaseError {
    public BadRequestError(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
