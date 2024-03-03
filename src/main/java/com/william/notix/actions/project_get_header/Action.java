package com.william.notix.actions.project_get_header;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.ProjectHeaderDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.ForbiddenHttpException;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("projectGetHeaderAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final AuthorityService authorityService;

    @GetMapping("/api/project/{projectId}")
    @Authenticated(true)
    public Response<ProjectHeaderDto> getMethodName(
        @PathVariable("projectId") Long projectId,
        @Caller User caller
    ) {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            ROLE userRole = authorityService
                .getUserProjectRole(caller.getId(), projectId)
                .orElseThrow(ForbiddenException::new);
            String projectImageId = project.getImage() != null
                ? project.getImage().getId().toString()
                : null;
            return new Response<ProjectHeaderDto>()
                .setData(
                    new ProjectHeaderDto()
                        .setId(project.getId().toString())
                        .setName(project.getName())
                        .setStartDate(project.getStartDate())
                        .setEndDate(project.getEndDate())
                        .setImageId(projectImageId)
                        .setOwnerId(project.getOwner().getId().toString())
                        .setRole(userRole)
                );
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (ForbiddenException e) {
            throw new ForbiddenHttpException();
        } catch (Exception e) {
            log.error("Error [GET] /api/project/{}", projectId);
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
