package com.william.notix.actions.project_create;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.InviteDto;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.ProjectService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller("projectCreateAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;

    @PostMapping("/api/project")
    @Authenticated(true)
    public Response<ProjectDto> action(
        @RequestBody @Valid Request request,
        @Caller User caller
    ) {
        try {
            Project createdProject = projectService
                .createProject(
                    new Project()
                        .setName(request.getName())
                        .setStartDate(request.getStartDate())
                        .setEndDate(request.getEndDate()),
                    caller.getId()
                )
                .orElseThrow(Exception::new);

            List<InviteDto> invites = Optional
                .ofNullable(request.getInvites())
                .orElse(Collections.emptyList());
            for (InviteDto invite : invites) {
                projectService.addMember(createdProject.getId(), invite);
            }

            return new Response<ProjectDto>()
                .setData(
                    new ProjectDto()
                        .setId(createdProject.getId().toString())
                        .setName(createdProject.getName())
                        .setStartDate(createdProject.getStartDate())
                        .setEndDate(createdProject.getEndDate())
                        .setOwnerId(
                            createdProject.getOwner().getId().toString()
                        )
                        .setImageId(
                            createdProject.getImage() != null
                                ? createdProject.getImage().getId().toString()
                                : null
                        )
                );
        } catch (Exception e) {
            log.error(
                "Error [POST] /api/post request:{}, callerId:{}",
                request.toString(),
                caller.getId()
            );
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
