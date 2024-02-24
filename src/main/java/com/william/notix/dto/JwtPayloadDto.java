package com.william.notix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class JwtPayloadDto {

    @NonNull private Long id;

    @NonNull private String tagName;

    @NonNull private String email;
}
