package com.william.notix.dto;

import com.william.notix.utils.values.ROLE;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MemberDto {

    @NonNull private ROLE role;

    @NonNull private String id;

    @NonNull private String email;

    @NonNull private String fullName;

    private String imageId;
}
