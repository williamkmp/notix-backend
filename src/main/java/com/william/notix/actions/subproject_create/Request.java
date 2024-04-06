package com.william.notix.actions.subproject_create;

import java.util.Date;

import lombok.NonNull;
import lombok.Value;

@Value
public class Request {

    @NonNull
    private String name;
    
    @NonNull
    private Date startDate;
    
    @NonNull
    private Date endDate;
}
