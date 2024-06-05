package com.william.notix.dto;

import com.william.notix.utils.values.FINDING_PROPERTY;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class FindingPropertyDto {
    private FINDING_PROPERTY property;
    private String value;
}
