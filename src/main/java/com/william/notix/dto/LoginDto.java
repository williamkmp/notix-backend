package com.william.notix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class LoginDto {

    @NonNull private UserDto user;

    @NonNull private TokenDto token;
}
