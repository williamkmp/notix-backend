package com.william.notix.exceptions.socket;

import org.springframework.http.HttpStatusCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StandardProjectSocketException extends Exception {
    
    private Long userId;
    private String sessionUuid;
    private Long projectId;
    private final String message;
    private final HttpStatusCode code;

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
