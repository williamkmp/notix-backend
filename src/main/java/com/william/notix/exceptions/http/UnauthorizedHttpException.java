package com.william.notix.exceptions.http;

import com.william.notix.utils.values.MESSAGES;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public final class UnauthorizedHttpException extends HttpStatusCodeException {

    public UnauthorizedHttpException() {
        super(HttpStatus.UNAUTHORIZED, MESSAGES.ERROR_UNAUTHORIZED);
    }
}
