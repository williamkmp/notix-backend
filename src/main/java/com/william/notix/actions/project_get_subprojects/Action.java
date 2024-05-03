package com.william.notix.actions.project_get_subprojects;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.SubprojectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.ForbiddenHttpException;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.ProjectService;
import com.william.notix.services.SubprojectService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("projectGetSubprojectsAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final SubprojectService subprojectService;
    private final AuthorityService authorityService;

    @GetMapping("/api/project/{projectId}/subprojects")
    @Authenticated(true)
    public Response<SubprojectDto[]> getMethodName(
        @PathVariable("projectId") Long projectId,
        @Caller User caller
    ) {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            authorityService
                .getUserProjectRole(caller.getId(), projectId)
                .orElseThrow(ForbiddenException::new);
            
            List<Subproject> subprojects = subprojectService.findAllByProject(project.getId());
            SubprojectDto[] responseBody = subprojects
                .stream()
                .map(subproject -> new SubprojectDto()
                    .setId(subproject.getId().toString())
                    .setName(subproject.getName())
                    .setStartDate(subproject.getStartDate())
                    .setEndDate(subproject.getEndDate())
                )
                .toList()
                .toArray(new SubprojectDto[0]);
            
            return new Response<SubprojectDto[]>()
                .setData(responseBody);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (ForbiddenException e) {
            throw new ForbiddenHttpException();
        } catch (Exception e) {
            log.error("Error [GET] /api/project/{}/subprojects", projectId);
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
