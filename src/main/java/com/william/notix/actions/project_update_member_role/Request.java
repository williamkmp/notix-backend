package com.william.notix.actions.project_update_member_role;

import com.william.notix.utils.values.PROJECT_ROLE;
import lombok.NonNull;
import lombok.Value;

@Value
public class Request {

    @NonNull private PROJECT_ROLE role;

    @NonNull private String id;

    @NonNull private String email;

    @NonNull private String fullName;

    private String imageId;
}
