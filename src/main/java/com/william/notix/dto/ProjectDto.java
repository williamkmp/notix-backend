package com.william.notix.dto;

import io.micrometer.common.lang.NonNull;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectDto {

    @NonNull private String id;

    @NonNull private String name;

    private String imageId;

    @NonNull private String ownerId;

    @NonNull private Date startDate;

    @NonNull private Date endDate;
}
