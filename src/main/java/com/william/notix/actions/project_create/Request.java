package com.william.notix.actions.project_create;

import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Value;

@Value
public class Request {

    @NotBlank(message = VALIDATION.REQUIRED)
    @Size(max = 255, message = VALIDATION.NUMBER_MAX + 255)
    private String name;

    @NotNull(message = VALIDATION.REQUIRED) @FutureOrPresent(message = VALIDATION.DTM_FUTURE)
    private Date startDate;

    @NotNull(message = VALIDATION.REQUIRED) @FutureOrPresent(message = VALIDATION.DTM_FUTURE)
    private Date endDate;
}
