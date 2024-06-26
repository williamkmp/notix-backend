package com.william.notix.dto;

import com.william.notix.utils.values.ROLE;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SubprojectHeaderDto {

    @NonNull private String id;

    @NonNull private String projectId;

    @NonNull private String name;

    @NonNull private Date startDate;

    @NonNull private Date endDate;

    @NonNull private ROLE role;

    private String imageId;
}
