package com.william.notix.actions.project_upload_attachment;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.ActionFileDto;
import com.william.notix.dto.FileDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectFileDetail;
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
import com.william.notix.services.LogService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.ACTION;
import com.william.notix.utils.values.FILE_TYPE;
import com.william.notix.utils.values.MESSAGES;
import com.william.notix.utils.values.ROLE;
import com.william.notix.utils.values.TOPIC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller("projectUploadAttachment")
@RequiredArgsConstructor
public class Action {

    private final FileService fileService;
    private final ProjectService projectService;
    private final AuthorityService authorityService;
    private final LogService logService;
    private final SimpMessagingTemplate socket;

    @Authenticated(true)
    @PostMapping("/api/project/{projectId}/attachment")
    public Response<FileDto> handle(
        @PathVariable("projectId") Long projectId,
        @RequestParam("file") MultipartFile file,
        @SessionUuid String sessionUuid,
        @Caller User uploader
    ) throws StandardProjectSocketException {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            ROLE uploaderRole = authorityService
                .getUserProjectRole(uploader.getId(), project.getId())
                .orElseThrow(UnauthorizedException::new);

            boolean canUploadAttachment =
                authorityService.roleCanOperateAttachment(uploaderRole);

            if (!canUploadAttachment) {
                throw new ForbiddenException();
            }

            File savedFile = fileService
                .saveMultipartFile(file)
                .orElseThrow(Exception::new);

            ProjectFileDetail fileDetail = projectService
                .addAttachmentToProject(
                    project.getId(),
                    uploader.getId(),
                    savedFile.getId()
                )
                .orElseThrow(Exception::new);

            socket.convertAndSend(
                TOPIC.projectFile(projectId),
                new ActionFileDto()
                    .setAction(ACTION.ADD)
                    .setType(FILE_TYPE.ATTACHMENT)
                    .setId(savedFile.getId().toString())
                    .setName(savedFile.getName())
                    .setContentType(savedFile.getContentType())
                    .setSize(Long.valueOf(file.getBytes().length))
                    .setUploaderId(uploader.getId().toString())
                    .setCreatedAt(savedFile.getCreatedAt())
            );

            logService.projectNewFile(fileDetail.getId());

            return new Response<FileDto>()
                .setMessage(MESSAGES.UPLOAD_SUCCESS)
                .setData(
                    new FileDto()
                        .setType(FILE_TYPE.ATTACHMENT)
                        .setId(savedFile.getId().toString())
                        .setName(savedFile.getName())
                        .setContentType(savedFile.getContentType())
                        .setSize(Long.valueOf(file.getBytes().length))
                        .setUploaderId(uploader.getId().toString())
                        .setCreatedAt(savedFile.getCreatedAt())
                );
        } catch (ResourceNotFoundException e) {
            throw new NotFoundProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(uploader.getId());
        } catch (UnauthorizedException e) {
            throw new UnauthorizedProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(uploader.getId());
        } catch (ForbiddenException e) {
            throw new ForbiddenProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(uploader.getId());
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [POST] /project/{}/attchment, callerId:{}")
                .addArgument(projectId.toString())
                .addArgument(uploader.getId().toString());
            e.printStackTrace();
            throw new DataConflictProjectException()
                .setSessionUuid(sessionUuid)
                .setProjectId(projectId)
                .setUserId(uploader.getId());
        }
    }
}
