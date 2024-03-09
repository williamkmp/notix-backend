package com.william.notix.dto;

import com.william.notix.utils.values.ACTION;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MemberActionDto {

    @NonNull private ACTION action;

    @NonNull private String id;

    @NonNull private String email;

    @NonNull private String fullName;

    private String imageId;
}
