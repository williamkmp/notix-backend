package com.william.notix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateFindingParameterDto {

    private Long subprojectId;
    private String findingName;
    private Long creatorId;
}
