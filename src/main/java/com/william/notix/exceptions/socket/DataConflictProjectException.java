package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatus;

/**
 * Indicates project data conflict during an operation , user should refresh the current project page
 */
public class DataConflictProjectException extends StandardProjectSocketException{
    public DataConflictProjectException() {
        super(HttpStatus.CONFLICT, "Project data error, please refresh your browser");
    }
}
