package com.william.notix.actions.project_delete_picture;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.FileService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.TOPIC;


@Slf4j
@Controller("projectDeletePictureAction")
@RequiredArgsConstructor
public class Action {
    
    private final ProjectService projectService;
    private final FileService fileService;
    private final SimpMessagingTemplate socket;

    @DeleteMapping("/project/{projectId}/picture")
    @Authenticated(true)
    public Response<ProjectDto> action(
        @PathVariable("projectId") Long projectId,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) {
        try {
            Project project = projectService.findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            
            ProjectDto updatedProjectData = new ProjectDto()
                .setId(project.getId().toString())
                .setName(project.getName())
                .setStartDate(project.getStartDate())
                .setEndDate(project.getEndDate())
                .setImageId(null)
                .setOwnerId(caller.getId().toString());

            if(project.getImage() == null) {
                return new Response<ProjectDto>()
                    .setData(updatedProjectData);
            }
            
            fileService.deleteImageOfProject(projectId);

            socket.convertAndSend(
                TOPIC.project(projectId),
                updatedProjectData
            );

            return new Response<ProjectDto>()
                .setData(updatedProjectData);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log.atError()
                .setMessage("Error [DELETE] /project/{}/picture, callerId:{}")
                .addArgument(projectId.toString())
                .addArgument(caller.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
    

}
