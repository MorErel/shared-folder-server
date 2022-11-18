package com.project.sharedfolderserver.v1.utils.error;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
/**
 * Error obeject
 * to pass in http Response (see Response.class)
 */
public class Error {
    private String name;
    private String message;
}