package com.william.notix.actions.project_update;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.ProjectPreviewDto;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.exceptions.socket.ForbiddenProjectException;
import com.william.notix.exceptions.socket.NotFoundProjectException;
import com.william.notix.exceptions.socket.StandardProjectSocketException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.KEY;
import com.william.notix.utils.values.PREVIEW_ACTION;
import com.william.notix.utils.values.TOPIC;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final AuthorityService authorityService;
    private final SimpMessagingTemplate socket;

    @MessageMapping("/project/{projectId}")
    public void updateProjectName(
        @DestinationVariable("projectId") Long projectId,
        @Payload ProjectDto newProjectData,
        @SessionUuid String sessionUuid,
        @Caller User caller
    ) throws StandardProjectSocketException {
        try {
            String USER_ID = KEY.STOMP_HEADER_CALLER_USER_ID;
            String SESSION = KEY.STOMP_HEADER_CALLER_SESSION_UUID;
            authorityService
                .getUserProjectRole(caller.getId(), projectId)
                .orElseThrow(ForbiddenException::new);

            Project updatedProject = projectService
                .updateProject(projectId, caller.getId(), newProjectData)
                .orElseThrow(ResourceNotFoundException::new);

            String imageId = updatedProject.getImage() != null
                ? updatedProject.getImage().getId().toString()
                : null;

            socket.convertAndSend(
                TOPIC.project(projectId),
                new ProjectDto()
                    .setId(updatedProject.getId().toString())
                    .setName(updatedProject.getName())
                    .setOwnerId(updatedProject.getOwner().getId().toString())
                    .setImageId(imageId)
                    .setStartDate(updatedProject.getStartDate())
                    .setEndDate(updatedProject.getEndDate()),
                Map.ofEntries(
                    Map.entry(USER_ID, caller.getId().toString()),
                    Map.entry(SESSION, sessionUuid)
                )
            );

            socket.convertAndSend(
                TOPIC.projectPreview(projectId),
                new ProjectPreviewDto()
                    .setAction(PREVIEW_ACTION.UPDATE)
                    .setId(projectId.toString())
                    .setName(updatedProject.getName())
                    .setImageId(imageId)
            );
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
                .addArgument(caller.getId())
                .addArgument(newProjectData.toString());
            e.printStackTrace();
        }
    }
}
