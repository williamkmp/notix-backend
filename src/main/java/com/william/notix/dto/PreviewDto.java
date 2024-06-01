package com.william.notix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class PreviewDto {

    @NonNull private String id;

    @NonNull private String name;

    private String imageId;
}
