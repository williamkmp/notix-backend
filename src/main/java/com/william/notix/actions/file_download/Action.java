package com.william.notix.actions.file_download;

import com.william.notix.entities.File;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("getFileAction")
@RequiredArgsConstructor
public class Action {

    private final FileService fileService;

    @GetMapping("/api/file/{fileId}")
    public ResponseEntity<byte[]> action(@PathVariable("fileId") Long fileId) {
        try {
            File file = fileService
                .findById(fileId)
                .orElseThrow(ResourceNotFoundException::new);

            String fileType = file.getContentType();
            String fileLength = Long.toString(file.getBytes().length);
            String disposition =
                "attachment; filename=\"" + file.getName() + "\"";
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, fileType)
                .header(HttpHeaders.CONTENT_LENGTH, fileLength)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(file.getBytes());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log.error("Error [GET] /api/file/{}", fileId);
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
