package com.william.notix.actions.project_create;

import com.william.notix.dto.InviteDto;
import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import lombok.Value;

@Value
public class Request {

    @NotBlank(message = VALIDATION.REQUIRED)
    @Size(max = 255, message = VALIDATION.NUMBER_MAX + 255)
    private String name;

    @NotNull(message = VALIDATION.REQUIRED) private Date startDate;

    @NotNull(message = VALIDATION.REQUIRED) private Date endDate;

    private List<InviteDto> invites;
}
