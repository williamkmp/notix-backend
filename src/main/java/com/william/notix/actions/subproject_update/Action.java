package com.william.notix.actions.subproject_update;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.SubprojectDto;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.SubprojectService;
import com.william.notix.utils.values.ROLE;

@Controller("subprojectUpdateAction")
public class Action {
    
    private final SubprojectService subprojectService;
    private final AuthorityService authorityService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/subproject/{subprojectId}.update")
    public void handle(
        @DestinationVariable("subprojectId") Long subprojectId,
        @Payload SubprojectDto subprojectData,
        @SessionUuid String sessionUuid,
        @Caller User caller
    ) {
        try {
            Subproject subproject = subprojectService
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);

            authorityService
                .getUserSubprojectRole(
                    caller.getId(), 
                    subproject.getId()
                )
                .orElseThrow(ForbiddenException::new);
            
            // TODO: implemnet this

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
