package com.william.notix.dto;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class LogDto {

    private String id;
    private String title;
    private String message;
    private String userId;
    private String projectId;
    private String subprojectId;
    private Date createdAt;
}
