package com.william.notix.exceptions.runtime;

public class MismatchedDataException extends RuntimeException {

    public MismatchedDataException() {
        super("Mismatched data exception");
    }

    public MismatchedDataException(String message) {
        super(message);
    }
}
