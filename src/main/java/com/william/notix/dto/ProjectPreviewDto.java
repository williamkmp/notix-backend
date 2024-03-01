package com.william.notix.dto;

import com.william.notix.utils.values.PREVIEW_ACTION;
import io.micrometer.common.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ProjectPreviewDto {

    @NonNull private PREVIEW_ACTION action;

    @NonNull private String id;

    @NonNull private String name;

    private String imageId;
}
