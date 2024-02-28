package com.william.notix.actions.user_get_affiliated_projects;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.DateTimeService;
import com.william.notix.services.ProjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller("userGetAffiliatedProjectsAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final DateTimeService dtmService;

    @GetMapping("/api/user/projects")
    @Authenticated(true)
    public Response<List<ProjectDto>> action(@Caller User caller) {
        try {
            List<Project> projects = projectService.findAllByUser(
                caller.getId()
            );
            return new Response<List<ProjectDto>>()
                .setData(
                    projects
                        .stream()
                        .map(project ->
                            new ProjectDto()
                                .setId(project.getId().toString())
                                .setName(project.getName())
                                .setOwnerId(
                                    project.getOwner().getId().toString()
                                )
                                .setImageId(
                                    project.getImage() != null
                                        ? project.getImage().getId().toString()
                                        : null
                                )
                                .setEndDate(
                                    dtmService.toOffsetDtm(project.getEndDate())
                                )
                                .setStartDate(
                                    dtmService.toOffsetDtm(
                                        project.getStartDate()
                                    )
                                )
                        )
                        .toList()
                );
        } catch (Exception e) {
            log.error(
                "Error [GET] /api/user/projects callerId:{}",
                caller.getId().toString()
            );
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
