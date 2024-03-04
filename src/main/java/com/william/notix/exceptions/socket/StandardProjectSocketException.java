package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatusCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StandardProjectSocketException extends Exception {
    
    private final Long userId;
    private final String sessionUuid;
    private final Long projectId;
    private final String message;
    private final HttpStatusCode code;

    public StandardProjectSocketException(
        Long userId,
        Long projectId,
        String sessionUuid,
        HttpStatusCode code,
        String message
    ) {
        this.userId = userId;
        this.projectId = projectId;
        this.sessionUuid = sessionUuid;
        this.code = code;
        this.message = message;
    }

    public StandardProjectSocketException(
        HttpStatusCode code,
        String message
    ) {
        this.userId = null;
        this.projectId = null;
        this.sessionUuid = null;
        this.code = code;
        this.message = message;
    }

}
