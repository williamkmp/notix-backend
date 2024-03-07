package com.william.notix.actions.project_get_logs;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.LogDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectLog;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.ForbiddenHttpException;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.LogService;
import com.william.notix.services.ProjectService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller("projectGetLogsAction")
@RequiredArgsConstructor
public class Action {

    private final LogService logService;
    private final AuthorityService authorityService;
    private final ProjectService projectService;

    @GetMapping("/api/project/{projectId}/logs")
    @Authenticated(true)
    public Response<LogDto[]> action(
        @PathVariable("projectId") Long projectId,
        @RequestParam(name = "skip", defaultValue = "0") int skipCount,
        @RequestParam(name = "take", defaultValue = "5") int takeCount,
        @Caller User caller
    ) {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            authorityService
                .getUserProjectRole(caller.getId(), project.getId())
                .orElseThrow(ForbiddenException::new);
            int pageNumber = skipCount / takeCount;
            Pageable paging = PageRequest.of(pageNumber, takeCount);
            List<ProjectLog> records = projectService
                .findLogsById(projectId, paging)
                .orElseThrow(Exception::new);
            List<LogDto> logDtoList = records
                .stream()
                .map(rec -> logService.serializeProjectLog(rec).orElse(null))
                .filter(Objects::nonNull)
                .toList();
            LogDto[] response = logDtoList.toArray(new LogDto[0]);
            return new Response<LogDto[]>().setData(response);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (ForbiddenException e) {
            throw new ForbiddenHttpException();
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [GET] /api/project/{}/logs, callerId:{}")
                .addArgument(projectId.toString())
                .addArgument(caller.getId().toString());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
