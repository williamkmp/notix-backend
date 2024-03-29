package com.william.notix.actions.subproject_create;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.entities.User;

import lombok.RequiredArgsConstructor;

@Controller("findingCreateAction")
@RequiredArgsConstructor
public class Action {
    
    @MessageMapping("/project/{projectId}/finding.add")
    public void action(
        @DestinationVariable("projectId") Long projectId,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) {
        // TODO: implement action
    }
}
