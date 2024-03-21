package com.william.notix.actions.project_add_member;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.InviteDto;
import com.william.notix.dto.MemberActionDto;
import com.william.notix.dto.PreviewActionDto;
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
import com.william.notix.utils.values.PREVIEW_ACTION;
import com.william.notix.utils.values.ROLE;
import com.william.notix.utils.values.TOPIC;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller("projectAddMemberAction")
@RequiredArgsConstructor
public class Action {

    private final AuthorityService authorityService;
    private final ProjectService projectService;
    private final LogService logService;
    private final SimpMessagingTemplate socket;

    @MessageMapping("/project/{projectId}/member.add")
    public void action(
        @DestinationVariable("projectId") Long projectId,
        @Payload List<InviteDto> invites,
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

            boolean canAddMember = authorityService.roleCanOperateMember(
                callerRole
            );
            if (!canAddMember) {
                throw new ForbiddenException();
            }

            String imageId = project.getImage() != null
                ? project.getImage().getId().toString()
                : null;

            for (InviteDto invite : invites) {
                Optional<User> invitedUser = projectService.addMember(
                    project.getId(),
                    invite
                );
                if (invitedUser.isEmpty()) {
                    continue;
                }
                User member = invitedUser.get();

                logService.userInvitedToProject(
                    caller.getId(),
                    invitedUser.get().getId(),
                    project.getId()
                );

                logService.projcetMemberAdded(
                    project.getId(),
                    invitedUser.get().getId()
                );

                User newMember = invitedUser.get();
                socket.convertAndSend(
                    TOPIC.userProjectPreviews(newMember.getId()),
                    new PreviewActionDto()
                        .setAction(PREVIEW_ACTION.ADD_CHILD)
                        .setId(project.getId().toString())
                        .setName(project.getName())
                        .setImageId(imageId)
                );

                String memberImageId = member.getImage() != null
                    ? member.getImage().getId().toString()
                    : null;

                socket.convertAndSend(
                    TOPIC.projectMembers(project.getId()),
                    new MemberActionDto()
                        .setAction(ACTION.ADD)
                        .setId(member.getId().toString())
                        .setEmail(member.getEmail())
                        .setFullName(member.getFullName())
                        .setImageId(memberImageId)
                        .setRole(invite.getRole())
                );
            }
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
                    "Error [STOMP] /project/{}/member.add, callerId:{}, payload:{}"
                )
                .addArgument(projectId.toString())
                .addArgument(caller.getId().toString())
                .addArgument(invites);
            e.printStackTrace();
            throw new DataConflictProjectException()
                .setSessionUuid(sessionUuid)    
                .setProjectId(projectId)
                .setUserId(caller.getId());
        }
    }
}
