package com.william.notix.actions.user_update;

import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class Request {

    @NotBlank(message = VALIDATION.REQUIRED)
    @Size(max = 255, message = VALIDATION.STRING_LENGTH + 1 + "," + 255)
    @Email(message = VALIDATION.STRING_EMAIL)
    private String email;

    @NotBlank(message = VALIDATION.REQUIRED)
    @Size(max = 255, message = VALIDATION.STRING_LENGTH + 1 + "," + 255)
    private String fullName;

    private final String imageId;
}
