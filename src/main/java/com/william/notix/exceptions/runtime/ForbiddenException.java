package com.william.notix.exceptions.runtime;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Unauthorized resource access");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
