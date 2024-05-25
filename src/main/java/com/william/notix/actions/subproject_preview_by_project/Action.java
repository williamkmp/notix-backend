package com.william.notix.actions.subproject_preview_by_project;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.PreviewActionDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.SubprojectService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("subprojectPreviewByProject")
@RequiredArgsConstructor
public class Action {

    private final SubprojectService subprojectService;

    @GetMapping("/api/project/{projectId}/subprojects/preview")
    @Authenticated(true)
    public Response<PreviewActionDto[]> action(
        @PathVariable("projectId") Long projectId,
        @Caller User caller
    ) {
        try {
            List<PreviewActionDto> previewList = subprojectService
                .findAllByProject(projectId)
                .stream()
                .map(subproject ->
                    new PreviewActionDto()
                        .setId(subproject.getId().toString())
                        .setName(subproject.getName())
                        .setImageId(
                            Objects.nonNull(subproject.getImage())
                                ? subproject.getImage().getId().toString()
                                : null
                        )
                )
                .toList();

            return new Response<PreviewActionDto[]>()
                .setData(previewList.toArray(new PreviewActionDto[0]));
        } catch (Exception e) {
            log
                .atError()
                .setMessage(
                    "Error [GET] /api/project/{}/subprojects/preview, callerId: {}"
                )
                .addArgument(projectId.toString())
                .addArgument(caller.getId().toString());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
