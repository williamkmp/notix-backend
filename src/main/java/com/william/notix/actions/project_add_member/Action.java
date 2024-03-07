package com.william.notix.actions.project_add_member;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.InviteDto;
import com.william.notix.entities.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("projectAddMemberAction")
@RequiredArgsConstructor
public class Action {
    
    @MessageMapping("/project/{projectId}/member.add")
    public void action(
        @DestinationVariable("projectId") Long projectId,
        @Payload List<InviteDto> invites,
        @Caller User caller
    ) {
        try {
            // TODO: implement add member
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [STOMP] /project/{}, callerId:{}, payload:{}")
                .addArgument(projectId.toString())
                .addArgument(caller.getId().toString())
                .addArgument(invites);
            e.printStackTrace();
        }
    }
}
