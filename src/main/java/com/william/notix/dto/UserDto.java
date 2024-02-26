package com.william.notix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserDto {

    @NonNull private String id;

    @NonNull private String email;

    @NonNull private String fullName;

    private String imageId;
}
