package com.william.notix.actions.upload_file;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.FileDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller("uploadFileAction")
@RequiredArgsConstructor
public class Action {

    private final FileService fileService;

    @PostMapping("/api/file")
    @Authenticated(true)
    public Response<FileDto> action(
        @Caller User uploader,
        @RequestParam("file") MultipartFile file
    ) {
        try {
            File savedFile = fileService
                .saveMultipartFile(file, uploader.getId())
                .orElseThrow(Exception::new);
            FileDto fileInformation = fileService
                .getFileInfo(savedFile.getId())
                .orElseThrow(Exception::new);
            return new Response<FileDto>().setData(fileInformation);
        } catch (Exception e) {
            log.error("Error, uploading file by, userId: {}", uploader.getId());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
