package com.william.notix.actions.project_update_member_role;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.MemberActionDto;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.MismatchedDataException;
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
import com.william.notix.services.UserService;
import com.william.notix.utils.values.ACTION;
import com.william.notix.utils.values.PROJECT_ROLE;
import com.william.notix.utils.values.ROLE;
import com.william.notix.utils.values.TOPIC;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller("projectUpdateMemberRoleAction")
@RequiredArgsConstructor
public class Action {

    private final AuthorityService authorityService;
    private final UserService userService;
    private final ProjectService projectService;
    private final LogService logService;
    private final SimpMessagingTemplate socket;

    @MessageMapping("/project/{projectId}/member.update")
    public void action(
        @DestinationVariable("projectId") Long projectId,
        @Payload Request payload,
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

            boolean canUpdateMember = authorityService.roleCanOperateMember(
                callerRole
            );
            if (!canUpdateMember) {
                throw new ForbiddenException();
            }

            Long memberId = Long.valueOf(payload.getId());
            User member = userService
                .findById(memberId)
                .orElseThrow(MismatchedDataException::new);

            ROLE prevRole = authorityService
                .getUserProjectRole(member.getId(), project.getId())
                .orElseThrow(MismatchedDataException::new);

            if (Objects.equals(prevRole, payload.getRole())) {
                return;
            }

            PROJECT_ROLE updatedRole = authorityService
                .updateMemberRole(
                    member.getId(),
                    project.getId(),
                    payload.getRole()
                )
                .orElseThrow(MismatchedDataException::new);

            String memberImageId = member.getImage() != null
                ? member.getImage().getId().toString()
                : null;

            logService.projectMemberRoleChange(
                project.getId(),
                member.getId(),
                updatedRole
            );

            socket.convertAndSend(
                TOPIC.projectMembers(projectId),
                new MemberActionDto()
                    .setAction(ACTION.UPDATE)
                    .setId(member.getId().toString())
                    .setEmail(member.getEmail())
                    .setFullName(member.getFullName())
                    .setImageId(memberImageId)
                    .setRole(authorityService.mapProjectRole(updatedRole))
            );
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
                    "Error [STOMP] /project/{}/member.update, callerId:{}, payload:{}"
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
