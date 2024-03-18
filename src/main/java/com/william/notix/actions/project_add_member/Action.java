package com.william.notix.actions.project_add_member;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.InviteDto;
import com.william.notix.dto.ProjectPreviewDto;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.exceptions.socket.ForbiddenProjectException;
import com.william.notix.exceptions.socket.NotFoundProjectException;
import com.william.notix.exceptions.socket.StandardProjectSocketException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.LogService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.PREVIEW_ACTION;
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

            authorityService
                .getUserProjectRole(caller.getId(), project.getId())
                .orElseThrow(ForbiddenException::new);

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

                logService.logUserIsInvitedToProject(
                    caller.getId(), 
                    invitedUser.get().getId(), 
                    projectId
                );
                
                User newMember = invitedUser.get();
                socket.convertAndSend(
                    TOPIC.userProjectPreviews(newMember.getId()),
                    new ProjectPreviewDto()
                        .setAction(PREVIEW_ACTION.ADD_CHILD)
                        .setId(project.getId().toString())
                        .setName(project.getName())
                        .setImageId(imageId)
                );
            }
        } catch (ResourceNotFoundException e) {
            throw new NotFoundProjectException()
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
                    "Error [STOMP] /project/{}, callerId:{}, payload:{}"
                )
                .addArgument(projectId.toString())
                .addArgument(caller.getId().toString())
                .addArgument(invites);
            e.printStackTrace();
        }
    }
}
