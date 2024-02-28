package com.william.notix.actions.auth_refresh;

import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class Request {

    @NotBlank(message = VALIDATION.REQUIRED)
    private final String refreshToken;
}
