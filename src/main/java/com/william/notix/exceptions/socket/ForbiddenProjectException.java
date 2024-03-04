package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatus;

/**
 * Indicates user have no access to this page, user should be redirected to the index page
 */
public class ForbiddenProjectException extends StandardProjectSocketException{
    public ForbiddenProjectException() {
        super(HttpStatus.FORBIDDEN, "You have no access to this project");
    }
}
