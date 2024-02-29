package com.william.notix.actions.projects_by_user;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.ProjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller("projectsByUserAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;

    @GetMapping("/api/user/projects")
    @Authenticated(true)
    public Response<ProjectDto[]> action(@Caller User caller) {
        try {
            List<Project> projects = projectService.findAllByUser(
                caller.getId()
            );
            List<ProjectDto> resultList = projects
                .stream()
                .map(project ->
                    new ProjectDto()
                        .setId(project.getId().toString())
                        .setName(project.getName())
                        .setOwnerId(project.getOwner().getId().toString())
                        .setStartDate(project.getStartDate())
                        .setEndDate(project.getEndDate())
                        .setImageId(
                            project.getImage() != null
                                ? project.getImage().getId().toString()
                                : null
                        )
                )
                .toList();

            return new Response<ProjectDto[]>()
                .setData(resultList.toArray(new ProjectDto[0]));
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
