package com.william.notix.actions.project_create;

import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Value;

@Value
public class Request {

    @NotBlank(message = VALIDATION.REQUIRED)
    @Size(max = 255, message = VALIDATION.NUMBER_MAX + 255)
    private String name;

    @NotEmpty(message = VALIDATION.REQUIRED)
    @FutureOrPresent(message = VALIDATION.DTM_FUTURE)
    private OffsetDateTime startDate;

    @NotEmpty(message = VALIDATION.REQUIRED)
    @FutureOrPresent(message = VALIDATION.DTM_FUTURE)
    private OffsetDateTime endDate;
}
