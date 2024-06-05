package com.william.notix.dto.parameter;

import com.william.notix.entities.Finding;
import com.william.notix.entities.User;
import com.william.notix.utils.values.FINDING_PROPERTY;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateFindingPropertyParameter {

    @NonNull private Finding finding;

    @NonNull private User actor;

    @NonNull private FINDING_PROPERTY targetProperty;

    @NonNull private String value;

    @NonNull private String sessionToken;
}
