package com.william.notix.dto;

import com.william.notix.entities.FindingDetail;
import com.william.notix.utils.values.ROLE;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@NoArgsConstructor
public class FindingDto {

    private String id;
    private String name;
    private ROLE role;
    private FindingDetail findingDetail;
    private String creatorId;
}
