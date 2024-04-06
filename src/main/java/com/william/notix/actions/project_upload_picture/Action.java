package com.william.notix.actions.project_upload_picture;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.FileDto;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.FileService;
import com.william.notix.services.ProjectService;
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
@Controller("projectUploadPictureAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final FileService fileService;
    private final SimpMessagingTemplate socket;

    @PostMapping("/project/{projectId}/picture")
    @Authenticated(true)
    public Response<ProjectDto> action(
        @PathVariable("projectId") Long projectId,
        @RequestParam("file") MultipartFile formData,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            File uploadedImage = fileService
                .saveMultipartFile(formData, caller.getId())
                .orElseThrow(Exception::new);

            FileDto imageInfo = fileService
                .updateProjectImage(project.getId(), uploadedImage.getId())
                .orElseThrow(Exception::new);

            ProjectDto updatedProjectData = new ProjectDto()
                .setId(project.getId().toString())
                .setName(project.getName())
                .setStartDate(project.getStartDate())
                .setEndDate(project.getEndDate())
                .setImageId(imageInfo.getId())
                .setOwnerId(caller.getId().toString());

            socket.convertAndSend(TOPIC.project(projectId), updatedProjectData);

            return new Response<ProjectDto>().setData(updatedProjectData);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [POST] /project/{}/picture, callerId:{}")
                .addArgument(projectId.toString())
                .addArgument(caller.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
