package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;

public class FileNameCannotBeEmpty extends BadRequestError {


    public FileNameCannotBeEmpty() {
        super(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
    }
}
