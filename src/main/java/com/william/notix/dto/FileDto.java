package com.william.notix.dto;

import com.william.notix.utils.values.FILE_TYPE;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class FileDto {

    private String id;
    private String uploaderId;
    private String name;
    private Long size;
    private String url;
    private FILE_TYPE type;
    private String contentType;
}
