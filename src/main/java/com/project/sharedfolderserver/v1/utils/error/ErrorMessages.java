package com.project.sharedfolderserver.v1.utils.error;

public interface ErrorMessages {
    String FILE_CANNOT_BE_CREATED_ERROR_MESSAGE = "file could not be created: ";
    String FILE_NAME_CANNOT_BE_EMPTY = "file name can not be empty ";
    String FILE_IS_NULL_ERROR_MESSAGE = "file is null ";
    String FILE_NAME_ALREADY_EXISTS = "file with this name already exists ";
    String ILLEGAL_FILE_NAME = "file name must be in the form of NAME.KIND using number, letters, dashes and underscores ";
    String FILE_CANNOT_BE_UPDATED = "file can not be updated, you can only update the name of the file. ";
}
