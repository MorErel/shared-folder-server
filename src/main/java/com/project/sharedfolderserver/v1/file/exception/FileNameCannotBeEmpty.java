package com.project.sharedfolderserver.v1.file.exception;

import com.project.sharedfolderserver.utils.http.error.BadRequestError;

public class FileNameCannotBeEmpty extends BadRequestError {


    public FileNameCannotBeEmpty(String fileName)
    {
        super(fileName);
    }
}
