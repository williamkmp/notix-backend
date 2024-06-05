package com.william.notix.actions.finding_update_detail;

import com.william.notix.utils.values.FINDING_PROPERTY;
import lombok.Value;

@Value
public class Request {

    private FINDING_PROPERTY property;
    private String value;
}
