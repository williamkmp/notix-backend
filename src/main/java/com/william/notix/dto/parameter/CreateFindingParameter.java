package com.william.notix.dto.parameter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateFindingParameter {

    private Long subprojectId;
    private String findingName;
    private Long creatorId;
}
