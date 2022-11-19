package com.project.sharedfolderserver.utils.http.error;

import com.project.sharedfolderserver.v1.utils.error.Error;
import com.project.sharedfolderserver.v1.utils.error.ErrorMessages;
import com.project.sharedfolderserver.v1.utils.http.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
@ResponseStatus
public class ErrorInterceptor {

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<Response<Object>> unexpectedExceptionHandler(Throwable exception) {
        Error error = new Error()
                .setName(exception.getClass().getSimpleName())
                .setMessage(String.format(ErrorMessages.UNEXPECTED_ERROR, exception.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(List.of(error)));
    }

    @ExceptionHandler(value = {BaseError.class})
    public ResponseEntity<Response<Object>> basicErrorHandler(BaseError exception) {
        Error error = ErrorBuilder.fromBasicError(exception);
        return ResponseEntity.status(exception.getStatus()).body(new Response<>(List.of(error)));
    }
}
