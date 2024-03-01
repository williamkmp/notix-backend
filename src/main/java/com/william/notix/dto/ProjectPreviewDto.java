package com.william.notix.dto;

import com.william.notix.utils.values.PREVIEW_ACTION;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
