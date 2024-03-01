package com.william.notix.dto;

import com.william.notix.utils.values.ROLE;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class InviteDto {

    @NonNull private String email;

    @NonNull private ROLE role;
}
