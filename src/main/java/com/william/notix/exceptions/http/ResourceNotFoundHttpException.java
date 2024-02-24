package com.william.notix.exceptions.http;

import com.william.notix.utils.values.MESSAGES;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public final class ResourceNotFoundHttpException
    extends HttpStatusCodeException {

    public ResourceNotFoundHttpException() {
        super(HttpStatus.BAD_REQUEST, MESSAGES.ERROR_RESOURCE_NOT_FOUND);
    }
}
