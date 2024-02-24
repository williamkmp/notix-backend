package com.william.notix.exceptions.http;

import com.william.notix.utils.values.MESSAGES;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class InternalServerErrorHttpException extends HttpStatusCodeException {

    public InternalServerErrorHttpException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGES.ERROR_INTERNAL_SERVER);
    }
}
