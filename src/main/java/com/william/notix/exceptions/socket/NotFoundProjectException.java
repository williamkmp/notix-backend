package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatus;

/**
 * Indicates project data is not found, user should be redirected to the index page
 */
public class NotFoundProjectException extends StandardProjectSocketException{
    public NotFoundProjectException() {
        super(HttpStatus.NOT_FOUND, "Project not found");
    }
}
