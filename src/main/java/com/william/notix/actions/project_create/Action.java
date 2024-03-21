package com.william.notix.actions.project_create;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.InviteDto;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.PreviewActionDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.ProjectService;
import com.william.notix.utils.values.PREVIEW_ACTION;
import com.william.notix.utils.values.TOPIC;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller("projectCreateAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final SimpMessagingTemplate socket;

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

            String imageId = createdProject.getImage() != null
                ? createdProject.getImage().getId().toString()
                : null;

            List<InviteDto> invites = Optional
                .ofNullable(request.getInvites())
                .orElse(Collections.emptyList());
            for (InviteDto invite : invites) {
                Optional<User> invitedUser = projectService.addMember(
                    createdProject.getId(),
                    invite
                );
                if (invitedUser.isEmpty()) {
                    continue;
                }
                User newMember = invitedUser.get();
                socket.convertAndSend(
                    TOPIC.userProjectPreviews(newMember.getId()),
                    new PreviewActionDto()
                        .setAction(PREVIEW_ACTION.ADD_CHILD)
                        .setId(createdProject.getId().toString())
                        .setName(createdProject.getName())
                        .setImageId(imageId)
                );
            }

            socket.convertAndSend(
                TOPIC.userProjectPreviews(caller.getId()),
                new PreviewActionDto()
                    .setAction(PREVIEW_ACTION.ADD_CHILD)
                    .setId(createdProject.getId().toString())
                    .setName(createdProject.getName())
                    .setImageId(imageId)
            );

            return new Response<ProjectDto>()
                .setData(
                    new ProjectDto()
                        .setId(createdProject.getId().toString())
                        .setName(createdProject.getName())
                        .setStartDate(createdProject.getStartDate())
                        .setEndDate(createdProject.getEndDate())
                        .setImageId(imageId)
                        .setOwnerId(
                            createdProject.getOwner().getId().toString()
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
