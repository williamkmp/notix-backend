package com.william.notix.actions.project_delete_member;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.MemberActionDto;
import com.william.notix.dto.MemberDto;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.exceptions.runtime.UnauthorizedException;
import com.william.notix.exceptions.socket.DataConflictProjectException;
import com.william.notix.exceptions.socket.ForbiddenProjectException;
import com.william.notix.exceptions.socket.NotFoundProjectException;
import com.william.notix.exceptions.socket.StandardProjectSocketException;
import com.william.notix.exceptions.socket.UnauthorizedProjectException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.LogService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.ACTION;
import com.william.notix.utils.values.ROLE;
import com.william.notix.utils.values.TOPIC;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller("projectDeleteMemberAction")
@RequiredArgsConstructor
public class Action {

    private final AuthorityService authorityService;
    private final ProjectService projectService;
    private final LogService logService;
    private final SimpMessagingTemplate socket;

    @MessageMapping("/project/{projectId}/member.delete")
    public void action(
        @DestinationVariable("projectId") Long projectId,
        @Payload MemberDto payload,
        @SessionUuid String sessionUuid,
        @Caller User caller
    ) throws StandardProjectSocketException {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            ROLE callerRole = authorityService
                .getUserProjectRole(caller.getId(), project.getId())
                .orElseThrow(UnauthorizedException::new);

            boolean canDeleteMember = authorityService.roleCanOperateMember(
                callerRole
            );

            if (!canDeleteMember) {
                throw new ForbiddenException();
            }

            Long memberId = Long.valueOf(payload.getId());
            Optional<User> maybeMember = projectService.deletePageMember(
                projectId,
                memberId
            );

            if (maybeMember.isEmpty()) {
                return;
            }

            User deletedMember = maybeMember.get();
            socket.convertAndSend(
                TOPIC.projectMembers(projectId),
                new MemberActionDto()
                    .setAction(ACTION.DELETE)
                    .setId(deletedMember.getId().toString())
                    .setFullName(deletedMember.getFullName())
                    .setEmail(deletedMember.getEmail())
            );
            logService.projectMemberDelete(projectId, memberId);

        } catch (ResourceNotFoundException e) {
            throw new NotFoundProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(caller.getId());
        } catch (UnauthorizedException e) {
            throw new UnauthorizedProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(caller.getId());
        } catch (ForbiddenException e) {
            throw new ForbiddenProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(caller.getId());
        } catch (Exception e) {
            log
                .atError()
                .setMessage(
                    "Error [STOMP] /project/{}/member.delete, callerId:{}, payload:{}"
                )
                .addArgument(projectId.toString())
                .addArgument(caller.getId().toString())
                .addArgument(payload);
            e.printStackTrace();
            throw new DataConflictProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(caller.getId());
        }
    }
}
