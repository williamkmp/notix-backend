package com.william.notix.actions.finding_preview_by_subproject;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.PreviewDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.FindingService;
import com.william.notix.services.SubprojectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("findingPreviewBySubproject")
@RequiredArgsConstructor
public class Action {

    private final SubprojectService subprojectService;
    private final FindingService findingService;

    @Authenticated(true)
    @GetMapping("/api/subproject/{subprojectId}/findings/preview")
    public Response<PreviewDto[]> action(
        @PathVariable("subprojectId") Long subprojectId,
        @Caller User caller
    ) {
        try {
            Subproject subproject = subprojectService
                .findById(subprojectId)
                .orElseThrow();

            List<PreviewDto> previewList = findingService
                .findAllBySubproject(subproject)
                .stream()
                .map(findingService::createPreview)
                .toList();

            return new Response<PreviewDto[]>()
                .setData(previewList.toArray(new PreviewDto[0]));
        } catch (Exception e) {
            log
                .atError()
                .setMessage(
                    "Error [GET] /api/subproject/{}/findings/preview, callerId: {}"
                )
                .addArgument(subprojectId.toString())
                .addArgument(caller.getId().toString());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
