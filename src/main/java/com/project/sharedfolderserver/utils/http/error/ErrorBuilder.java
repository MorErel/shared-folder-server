package com.project.sharedfolderserver.utils.http.error;

import com.project.sharedfolderserver.v1.utils.error.Error;

public class ErrorBuilder {

    public static Error fromBasicError(BasicError exception) {
        return new Error()
                .setName(exception.getClass().getSimpleName())
                .setMessage(exception.getMessage());
    }
}
