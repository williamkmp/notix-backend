package com.william.notix.actions.subproject_upload_picture;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.FileDto;
import com.william.notix.dto.SubprojectDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.FileService;
import com.william.notix.services.SubprojectService;
import com.william.notix.utils.values.TOPIC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller("subprojectUploadPictureAction")
@RequiredArgsConstructor
public class Action {

    private final SubprojectService subprojectService;
    private final FileService fileService;
    private final SimpMessagingTemplate socket;

    @PostMapping("/subproject/{subprojectId}/picture")
    @Authenticated(true)
    public Response<SubprojectDto> action(
        @PathVariable("subprojectId") Long subprojectId,
        @RequestParam("file") MultipartFile formData,
        @Caller User caller,
        @SessionUuid String sessionUuid
    ) {
        try {
            Subproject subproject = subprojectService
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);

            File uploadedImage = fileService
                .saveMultipartFile(formData)
                .orElseThrow(Exception::new);

            FileDto imageInfo = fileService
                .updateSubprojectImage(
                    subproject.getId(),
                    uploadedImage.getId()
                )
                .orElseThrow(Exception::new);

            SubprojectDto updatedSubprojectData = new SubprojectDto()
                .setId(subproject.getId().toString())
                .setProjectId(subproject.getProject().getId().toString())
                .setName(subproject.getName())
                .setStartDate(subproject.getStartDate())
                .setEndDate(subproject.getEndDate())
                .setImageId(imageInfo.getId());

            socket.convertAndSend(
                TOPIC.subproject(subprojectId),
                updatedSubprojectData
            );

            return new Response<SubprojectDto>().setData(updatedSubprojectData);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log
                .atError()
                .setMessage("Error [POST] /subproject/{}/picture, callerId:{}")
                .addArgument(subprojectId.toString())
                .addArgument(caller.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
