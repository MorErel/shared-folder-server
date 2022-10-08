package com.project.sharedfolderserver.v1.utils.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.sharedfolderserver.v1.utils.error.Error;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T> {

    private T data;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Error> errors = new ArrayList<>();

    public Response(T data) {
        this.data = data;
    }

    public Response(List<Error> errors) {
        this.errors = errors;
    }
}
