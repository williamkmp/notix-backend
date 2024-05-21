package com.william.notix.dto;

import com.william.notix.utils.values.ACTION;
import com.william.notix.utils.values.FILE_TYPE;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ActionFileDto {

    private ACTION action;
    private FILE_TYPE type;
    private String id;
    private String uploaderId;
    private String name;
    private Long size;
    private String url;
    private String contentType;
    private Date createdAt;
}
