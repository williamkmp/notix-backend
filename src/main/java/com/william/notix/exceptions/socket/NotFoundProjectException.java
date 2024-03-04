package com.william.notix.exceptions.socket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * Indicates project data is not found, user should be redirected to the index page
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class NotFoundProjectException extends StandardProjectSocketException {

    public NotFoundProjectException() {
        super(HttpStatus.NOT_FOUND, "Project not found");
    }
}
