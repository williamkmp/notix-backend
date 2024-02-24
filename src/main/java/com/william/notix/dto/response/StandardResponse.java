package com.william.notix.dto.response;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class StandardResponse<T> {

    @NonNull private Integer status;

    private T data;
    private String message;
    private Map<String, String> error;
}
