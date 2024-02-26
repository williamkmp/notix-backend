package com.william.notix.services;

import java.sql.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.william.notix.dto.FileDto;
import com.william.notix.entities.File;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.repositories.FileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private String GET_FILE_URL = "http://localhost:5050/api/file/";
    private final FileRepository fileRepository;

    /**
     * get file by id
     * 
     * @param fileId {@link Long} file id
     * @return {@link Optional}<{@link File}> requested file, else empty if not found
     */
    public Optional<File> findById(Long fileId) {
        try {
            File file = fileRepository.findById(fileId)
            .orElseThrow(ResourceNotFoundException::new);
            return Optional.of(file); 
        } catch (Exception e) {
            log.error("File not found id:{}", fileId);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * save multipart-file request to the database
     * 
     * @param file {@link MultipartFile} file
     * @return  {@link Optional}<{@link File}> saved file entity, else empty if failed
     */
    public Optional<File> saveMultipartFile(MultipartFile file) {
        try {
            File newFile = new File()
                .setName(file.getOriginalFilename())
                .setBytes(file.getBytes())
                .setContentType(file.getContentType());
            newFile = fileRepository.saveAndFlush(newFile);
            return Optional.of(newFile);
        } catch (Exception e) {
            log.error("Error saving file");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * generate file prview information including:
     * <ul>
     *     <li>file id</li>
     *     <li>file name</li>
     *     <li>file size in BYTES</li>
     *     <li>file content-type</li>
     *     <li>file download URL</li>
     * </ul> 
     * @param fileId {@link Long} fileid
     * @return  {@link Optional}<{@link FileDto}> file information, else empty of there's an error generating file data or not found 
     */
    public Optional<FileDto> getFileInfo(Long fileId) {
        try {
            File file = fileRepository.findById(fileId)
                .orElseThrow(ResourceNotFoundException::new);
            return Optional.of(new FileDto()
                .setId(file.getId().toString())
                .setName(file.getName())
                .setContentType(file.getContentType())
                .setUrl(GET_FILE_URL + file.getId().toString())
                .setSize(Long.valueOf(file.getBytes().length))
            );
        } 
        catch (ResourceNotFoundException e) {
            return Optional.empty();
        } 
        catch (Exception e) {
            log.error("Error generating file info id: {}", fileId);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}