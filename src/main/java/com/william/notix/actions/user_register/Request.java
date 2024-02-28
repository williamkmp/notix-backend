package com.william.notix.actions.user_register;

import com.william.notix.utils.values.PATTERN;
import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = VALIDATION.REQUIRED)
    @Size(max = 255, message = VALIDATION.STRING_LENGTH + 1 + "," + 255)
    @Pattern(regexp = PATTERN.ALPHANUM, message = VALIDATION.STRING_ALPHANUM)
    private String password;
}
