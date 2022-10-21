package com.project.sharedfolderserver.v1.utils.error;

public interface ErrorMessages {
    String FILE_CANNOT_BE_CREATED_ERROR_MESSAGE = "file could not be created. ";
    String FILE_CANNOT_BE_DELETED_ERROR_MESSAGE = "file could not be deleted %s";
    String FILE_NAME_CANNOT_BE_EMPTY = "file name can not be empty";
    String FILE_IS_NULL_ERROR_MESSAGE = "file is null";
    String FILE_NAME_ALREADY_EXISTS = "file with the name %s already exists";
    String ILLEGAL_FILE_NAME = "Illegal file name %s, file name must be in the form of NAME.KIND, using letters, numbers. some special characters are illegal";
    String FILE_CANNOT_BE_UPDATED = "file can not be updated %s";
    String FILE_NOT_FOUND = "file with id %s does not exist";
    String UNEXPECTED_ERROR = "unexpected error %s";
    String REQUEST_VALIDATION_ERROR = "validation error %s";
}
