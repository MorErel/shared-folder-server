package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class IllegalFileName extends BadRequestError {

    private final static String MESSAGE = "file name %s is illegal " + ErrorMessages.ILLEGAL_FILE_NAME;

    public IllegalFileName(String fileName) {
        super(String.format(MESSAGE, fileName));
    }
}
