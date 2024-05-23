package com.william.notix.actions.project_delete_file;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.ActionFileDto;
import com.william.notix.dto.FileDto;
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
import com.william.notix.services.FileService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.ACTION;
import com.william.notix.utils.values.TOPIC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller("projectDeleteFileAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final AuthorityService authorityService;
    private final FileService fileService;
    private final SimpMessagingTemplate socket;

    @MessageMapping("/project/{projectId}/file.delete")
    public void handle(
        @DestinationVariable("projectId") Long projectId,
        @Payload Request payload,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) throws StandardProjectSocketException {
        try {
            Long targetFileId = Long.valueOf(payload.getFileId());

            projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            Boolean canDelete = authorityService
                .userCanDeleteProjectFile(caller.getId(), targetFileId)
                .orElseThrow(UnauthorizedException::new);

            if (Boolean.FALSE == canDelete) {
                throw new ForbiddenException();
            }

            FileDto fileInfo = fileService
                .getFileInfo(targetFileId)
                .orElseThrow();

            fileService.deleteProjectFileById(targetFileId);

            socket.convertAndSend(
                TOPIC.projectFile(projectId),
                new ActionFileDto()
                    .setAction(ACTION.DELETE)
                    .setId(fileInfo.getId())
                    .setType(fileInfo.getType())
                    .setName(fileInfo.getName())
                    .setContentType(fileInfo.getContentType())
                    .setSize(fileInfo.getSize())
                    .setUploaderId(fileInfo.getUploaderId())
                    .setCreatedAt(fileInfo.getCreatedAt())
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
                    "Error [STOMP] /project/{}/files.delete, callerId:{}, payload:{}"
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
