package com.william.notix.actions.finding_get;

import com.william.notix.dto.FindingDto;
import com.william.notix.dto.UserDto;
import com.william.notix.utils.values.ROLE;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ResponseBody {

    private FindingDto finding;
    private ROLE role;
    private UserDto creator;
}
