package com.william.notix.actions.project_create;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.DateTimeService;
import com.william.notix.services.ProjectService;
import jakarta.validation.Valid;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller("projectCreateAction")
@RequiredArgsConstructor
public class Action {

    private final DateTimeService dateTimeService;
    private final ProjectService projectService;

    @PostMapping("/api/post")
    public Response<ProjectDto> action(
        @RequestBody @Valid Request request,
        @Caller User caller
    ) {
        try {
            Date startDate = dateTimeService.toDate(request.getStartDate());
            Date endDate = dateTimeService.toDate(request.getEndDate());
            Project createdProject = projectService
                .createProject(
                    new Project()
                        .setName(request.getName())
                        .setStartDate(startDate)
                        .setEndDate(endDate),
                    caller.getId()
                )
                .orElseThrow(Exception::new);
            return new Response<ProjectDto>()
                .setData(
                    new ProjectDto()
                        .setId(createdProject.getId().toString())
                        .setName(createdProject.getName())
                        .setOwnerId(
                            createdProject.getOwner().getId().toString()
                        )
                        .setStartDate(
                            dateTimeService.toOffsetDtm(
                                createdProject.getStartDate()
                            )
                        )
                        .setEndDate(
                            dateTimeService.toOffsetDtm(
                                createdProject.getEndDate()
                            )
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
