package com.william.notix.dto;

import com.william.notix.entities.Cvss;
import com.william.notix.entities.FindingProperty;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class FindingDto {

    private String id;
    private String name;
    private String creatorId;
    private Date createdAt;
    private FindingProperty findingDetail;
    private Cvss cvssDetail;
}
