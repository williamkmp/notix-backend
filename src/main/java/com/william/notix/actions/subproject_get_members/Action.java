package com.william.notix.actions.subproject_get_members;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.MemberDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.ForbiddenHttpException;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.SubprojectService;
import com.william.notix.services.UserService;
import com.william.notix.utils.values.ROLE;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("subprojectGetMembers")
@RequiredArgsConstructor
public class Action {

    private final SubprojectService subprojectService;
    private final UserService userService;
    private final AuthorityService authorityService;

    @GetMapping("/api/subproject/{subprojectId}/members")
    @Authenticated(true)
    public Response<MemberDto[]> action(
        @PathVariable("subprojectId") Long subprojectId,
        @Caller User caller
    ) {
        try {
            Subproject subproject = subprojectService
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);

            authorityService
                .getUserSubprojectRole(caller.getId(), subproject.getId())
                .orElseThrow(ForbiddenException::new);

            List<User> memberList = userService
                .findAllByProject(subproject.getProject().getId())
                .orElseThrow();

            List<MemberDto> members = new ArrayList<>();
            for (User member : memberList) {
                Optional<ROLE> role = authorityService.getUserSubprojectRole(
                    caller.getId(),
                    subproject.getId()
                );

                if (role.isEmpty()) continue;

                String imageId = Objects.nonNull(member.getImage())
                    ? member.getImage().getId().toString()
                    : null;

                members.add(
                    new MemberDto()
                        .setId(member.getId().toString())
                        .setFullName(member.getFullName())
                        .setEmail(member.getEmail())
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
                .setMessage(
                    "Error [GET] /api/subproject/{}/members, callerId:{}"
                )
                .addArgument(subprojectId.toString())
                .addArgument(caller.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
