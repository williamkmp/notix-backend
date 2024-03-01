package com.william.notix.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserlogDto {

    private String id;
    private String message;
    private String userId;
    private String projectId;
    private String subprojectId;
    private String findingId;
}
