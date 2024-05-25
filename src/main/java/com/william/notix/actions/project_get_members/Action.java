package com.william.notix.actions.project_get_members;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.MemberDto;
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
import com.william.notix.services.UserService;
import com.william.notix.utils.values.ROLE;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("projectGetMemberAction")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final UserService userService;
    private final AuthorityService authorityService;

    @GetMapping("/api/project/{projectId}/members")
    @Authenticated(true)
    public Response<MemberDto[]> action(
        @PathVariable("projectId") Long projectId,
        @Caller User caller
    ) {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            authorityService
                .getUserProjectRole(caller.getId(), project.getId())
                .orElseThrow(ForbiddenException::new);

            List<User> users = userService
                .findAllByProject(projectId)
                .orElseThrow(Exception::new);

            List<MemberDto> members = new ArrayList<>();
            for (User user : users) {
                Optional<ROLE> role = authorityService.getUserProjectRole(
                    user.getId(),
                    project.getId()
                );

                if (role.isEmpty()) {
                    continue;
                }

                String imageId = user.getImage() != null
                    ? user.getImage().getId().toString()
                    : null;

                members.add(
                    new MemberDto()
                        .setId(user.getId().toString())
                        .setFullName(user.getFullName())
                        .setEmail(user.getEmail())
                        .setImageId(imageId)
                        .setRole(role.get())
                );
            }
            return new Response<MemberDto[]>()
                .setData(members.toArray(new MemberDto[0]));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (ForbiddenException e) {
            throw new ForbiddenHttpException();
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [GET] /api/project/{}/members, callerId:{}")
                .addArgument(projectId.toString())
                .addArgument(caller.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
