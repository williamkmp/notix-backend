package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Indicates project data conflict during an operation , user should refresh the current project page
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DataConflictProjectException extends StandardProjectSocketException{
    public DataConflictProjectException() {
        super(HttpStatus.CONFLICT, "Project data error, please refresh your browser");
    }
}
