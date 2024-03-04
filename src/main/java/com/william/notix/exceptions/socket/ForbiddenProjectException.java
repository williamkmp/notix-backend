package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Indicates user have no access to this page, user should be redirected to the index page
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ForbiddenProjectException extends StandardProjectSocketException{
    public ForbiddenProjectException() {
        super(HttpStatus.FORBIDDEN, "You have no access to this project");
    }
}
