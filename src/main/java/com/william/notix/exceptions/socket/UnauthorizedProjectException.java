package com.william.notix.exceptions.socket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * Indicates user have no access to this page, user should be redirected to the index page
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UnauthorizedProjectException
    extends StandardProjectSocketException {

    public UnauthorizedProjectException() {
        super(HttpStatus.UNAUTHORIZED, "Your request is unauthorized");
    }
}
