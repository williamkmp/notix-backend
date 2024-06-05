package com.william.notix.dto;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class FindingLogDto {

    private String id;
    private String title;
    private String message;
    private String actorId;
    private String findingId;
    private Date createdAt;
}
