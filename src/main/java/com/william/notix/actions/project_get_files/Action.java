package com.william.notix.actions.project_get_files;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.FileDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.FileService;
import com.william.notix.services.ProjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller("projectGetFiles")
@RequiredArgsConstructor
public class Action {

    private final ProjectService projectService;
    private final FileService fileService;

    @GetMapping("/project/{projectId}/picture")
    @Authenticated(true)
    public Response<FileDto[]> action(
        @PathVariable("projectId") Long projectId,
        @RequestParam("file") MultipartFile formData,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) {
        try {
            Project project = projectService
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);

            List<File> projectFiles = fileService
                .getProjectFiles(project.getId())
                .orElseThrow();

            List<FileDto> fileInfoList = projectFiles
                .stream()
                .map(file -> fileService.getFileInfo(file.getId()).orElse(null))
                .toList();

            return new Response<FileDto[]>()
                .setData(fileInfoList.toArray(new FileDto[0]));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [POST] /project/{}/picture, callerId:{}")
                .addArgument(projectId.toString())
                .addArgument(caller.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
