package com.william.notix.actions.subproject_create;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.SubprojectActionDto;
import com.william.notix.entities.Project;
import com.william.notix.entities.Subproject;
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
import com.william.notix.services.ProjectService;
import com.william.notix.services.SubprojectService;
import com.william.notix.utils.values.ACTION;
import com.william.notix.utils.values.ROLE;
import com.william.notix.utils.values.TOPIC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller("subprojectCreateAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final AuthorityService authorityService;
    private final SubprojectService subprojectService;
    private final SimpMessagingTemplate socket;

    @MessageMapping("/project/{projectId}/subproject.add")
    public void action(
        @DestinationVariable("projectId") Long projectId,
        @Payload Request payload,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) throws StandardProjectSocketException {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            ROLE callerRole = authorityService
                .getUserProjectRole(caller.getId(), projectId)
                .orElseThrow(UnauthorizedException::new);

            boolean canAddOperateSubproject =
                authorityService.roleCanAddSubproject(callerRole);

            if (!canAddOperateSubproject) {
                throw new ForbiddenException();
            }

            Subproject createdSubproject = subprojectService
                .addSubproject(
                    project.getId(),
                    new Subproject()
                        .setName(payload.getName())
                        .setStartDate(payload.getStartDate())
                        .setEndDate(payload.getEndDate())
                )
                .orElseThrow(Exception::new);

            socket.convertAndSend(
                TOPIC.projectSubprojects(project.getId()),
                new SubprojectActionDto()
                    .setAction(ACTION.ADD)
                    .setName(createdSubproject.getName())
                    .setProjectId(project.getId().toString())
                    .setId(createdSubproject.getId().toString())
                    .setStartDate(createdSubproject.getStartDate())
                    .setStartDate(createdSubproject.getEndDate())
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