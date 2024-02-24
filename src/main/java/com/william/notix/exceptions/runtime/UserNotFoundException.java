package com.william.notix.exceptions.runtime;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found exception");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
