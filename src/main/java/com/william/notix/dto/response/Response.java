package com.william.notix.dto.response;

import com.william.notix.utils.values.KEY;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@EqualsAndHashCode(callSuper = true)
public class Response<T> extends ResponseEntity<StandardResponse<T>> {

    private final StandardResponse<T> responseBody;

    public Response(HttpStatusCode status) {
        super(new StandardResponse<>(), status);
        this.responseBody = super.getBody();
        this.responseBody.setStatus(status.value());
    }

    public Response() {
        super(new StandardResponse<>(), HttpStatus.OK);
        this.responseBody = super.getBody();
        this.responseBody.setStatus(HttpStatus.OK.value());
    }

    public Response<T> setData(@NonNull T data) {
        this.responseBody.setData(data);
        return this;
    }

    public Response<T> setMessage(String message) {
        this.responseBody.setMessage(message);
        return this;
    }

    public Response<T> setError(Map<String, String> errorMap) {
        this.responseBody.setError(errorMap);
        return this;
    }

    public Response<T> addError(String path, String errorCode) {
        if (this.responseBody.getError() == null) {
            this.responseBody.setError(new HashMap<>());
        }
        this.responseBody.getError().put(path, errorCode);
        return this;
    }

    public Response<T> setRootError(String errorCode) {
        if (this.responseBody.getError() == null) {
            this.responseBody.setError(new HashMap<>());
        }
        this.responseBody.getError().put(KEY.RESPONSE_ERROR_ROOT, errorCode);
        return this;
    }
}
