package com.william.notix.actions.subproject_get_header;

import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.SubprojectHeaderDto;
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
import com.william.notix.utils.values.ROLE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("subprojectGetHeader")
@RequiredArgsConstructor
public class Action {
    
    private final SubprojectService subprojectService;
    private final AuthorityService authorityService;

    @GetMapping("/api/subproject/{subprojectId}")
    @Authenticated(true)
    public Response<SubprojectHeaderDto> action (
        @PathVariable("subprojectId") Long subprojectId,
        @Caller User caller
    ) {
        try {
            Subproject subproject = subprojectService
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);

            ROLE userRole = authorityService
                .getUserSubprojectRole(caller.getId(), subprojectId)
                .orElseThrow(ForbiddenException::new);

            String imageId = Objects.nonNull(subproject.getImage())
                ? subproject.getImage().getId().toString()
                : null;  

            return new Response<SubprojectHeaderDto>()
                .setData(
                    new SubprojectHeaderDto()
                        .setId(subproject.getId().toString())
                        .setName(subproject.getName())
                        .setProjectId(subproject.getProject().getId().toString())
                        .setImageId(imageId)
                        .setRole(userRole)
                        .setStartDate(subproject.getStartDate())
                        .setEndDate(subproject.getEndDate())
                );
            
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (ForbiddenException e) {
            throw new ForbiddenHttpException();
        } catch (Exception e) {
            log.error("Error [GET] /api/subproject/{}", subprojectId);
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }


}
