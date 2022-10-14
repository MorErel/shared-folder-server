package com.project.sharedfolderserver.utils.http.error;

import com.project.sharedfolderserver.v1.file.FileDto;
import com.project.sharedfolderserver.v1.utils.error.Error;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import com.project.sharedfolderserver.v1.utils.http.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ErrorInterceptor {

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<Response<Object>> nullExceptionHandler(NullPointerException exception) {
        Error nullError = new Error()
                .setName(exception.getClass().getSimpleName())
                .setMessage(ErrorMessages.FILE_NAME_CANNOT_BE_EMPTY);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(List.of(nullError)));
    }

    @ExceptionHandler(value = {BasicError.class})
    public ResponseEntity<Response<Object>> basicErrorHandler(BasicError exception) {
        Error error = ErrorBuilder.fromBasicError(exception);
        return ResponseEntity.status(exception.getStatus()).body(new Response<>(List.of(error)));
    }
}
