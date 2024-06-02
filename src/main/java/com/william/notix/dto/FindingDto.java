package com.william.notix.dto;

import com.william.notix.entities.CvssDetail;
import com.william.notix.entities.FindingDetail;
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
    private FindingDetail findingDetail;
    private CvssDetail cvssDetail;
}
