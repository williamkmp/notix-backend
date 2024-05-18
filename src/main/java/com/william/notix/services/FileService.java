package com.william.notix.services;

import com.william.notix.dto.FileDto;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectFileDetail;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.SubprojectFileDetail;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.repositories.FileRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private String GET_FILE_URL = "http://localhost:5050/api/file/";
    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    /**
     * get file by id
     *
     * @param fileId {@link Long} file id
     * @return {@link Optional}<{@link File}> requested file, else empty if not found
     */
    public Optional<File> findById(Long fileId) {
        try {
            if (fileId == null) throw new ResourceNotFoundException();
            File file = fileRepository
                .findById(fileId)
                .orElseThrow(ResourceNotFoundException::new);
            return Optional.of(file);
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
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
    public Optional<File> saveMultipartFile(@NonNull MultipartFile file) {
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
     * update existing user image
     *
     * @param userId {@link Long} user id, not null
     * @param newImageId {@link Long} file id, not null
     * @return {@link Optional}<{@link FileDto}> image file information
     */
    @Transactional
    public Optional<FileDto> updateUserImage(
        @NonNull Long userId,
        @NonNull Long newImageId
    ) {
        try {
            User user = userRepository
                .findById(userId)
                .orElseThrow(ResourceNotFoundException::new);
            File previousImage = user.getImage();
            if (
                previousImage != null &&
                !previousImage.getId().equals(newImageId)
            ) {
                fileRepository.delete(previousImage);
            }
            File newImage = fileRepository
                .findById(newImageId)
                .orElseThrow(ResourceNotFoundException::new);
            user.setImage(newImage);
            userRepository.save(user);
            FileDto fileInfo = getFileInfo(newImageId)
                .orElseThrow(Exception::new);
            return Optional.of(fileInfo);
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error(
                "Error updating user image, userId: {}, imageId: {}",
                userId,
                newImageId
            );
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * update existing project image
     *
     * @param projectId {@link Long} project id, not null
     * @param newImageId {@link Long} file id, not null
     * @return {@link Optional}<{@link FileDto}> image file information
     */
    @Transactional
    public Optional<FileDto> updateProjectImage(
        @NonNull Long projectId,
        @NonNull Long newImageId
    ) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            File previousImage = project.getImage();
            if (
                previousImage != null &&
                !previousImage.getId().equals(newImageId)
            ) {
                fileRepository.delete(previousImage);
            }
            File newImage = fileRepository
                .findById(newImageId)
                .orElseThrow(ResourceNotFoundException::new);
            project.setImage(newImage);
            projectRepository.save(project);
            FileDto fileInfo = getFileInfo(newImageId)
                .orElseThrow(Exception::new);
            return Optional.of(fileInfo);
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error(
                "Error updating project image, projectId: {}, imageId: {}",
                projectId,
                newImageId
            );
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * update existing subproject image
     *
     * @param projectId {@link Long} project id, not null
     * @param newImageId {@link Long} file id, not null
     * @return {@link Optional}<{@link FileDto}> image file information
     */
    @Transactional
    public Optional<FileDto> updateSubprojectImage(
        @NonNull Long subprojectId,
        @NonNull Long newImageId
    ) {
        try {
            Subproject subproject = subprojectRepository
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);
            File previousImage = subproject.getImage();
            if (
                previousImage != null &&
                !previousImage.getId().equals(newImageId)
            ) {
                fileRepository.delete(previousImage);
            }
            File newImage = fileRepository
                .findById(newImageId)
                .orElseThrow(ResourceNotFoundException::new);
            subproject.setImage(newImage);
            subprojectRepository.save(subproject);
            FileDto fileInfo = getFileInfo(newImageId)
                .orElseThrow(Exception::new);
            return Optional.of(fileInfo);
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error(
                "Error updating subproject image, subprojectId: {}, imageId: {}",
                subprojectId,
                newImageId
            );
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * delete existing user image
     * @param userId {@link Long} user id, not null
     */
    public void deleteUserImage(@NonNull Long userId) {
        try {
            User user = userRepository
                .findById(userId)
                .orElseThrow(ResourceNotFoundException::new);
            File previousImage = user.getImage();
            if (previousImage != null) {
                fileRepository.delete(previousImage);
            }
            user.setImage(null);
            userRepository.saveAndFlush(user);
        } catch (Exception e) {
            log.error("Error deleting user image, userId: {}", userId);
            e.printStackTrace();
        }
    }

    /**
     * delete existing project image
     * @param projectId {@link Long} project id, not null
     */
    @Transactional
    public void deleteImageOfProject(@NonNull Long projectId) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            File previousImage = project.getImage();
            if (previousImage != null) {
                fileRepository.delete(previousImage);
            }
            project.setImage(null);
            projectRepository.saveAndFlush(project);
        } catch (Exception e) {
            log.error("Error deleting project image, projectId: {}", projectId);
            e.printStackTrace();
        }
    }

    /**
     * delete existing subproject image
     * @param subprojectId {@link Long} subproject id
     */
    @Transactional
    public void deleteImageOfSubproject(@NonNull Long subprojectId) {
        try {
            Subproject subproject = subprojectRepository
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);
            File previousImage = subproject.getImage();
            if (!Objects.isNull(previousImage)) {
                fileRepository.delete(previousImage);
            }
            subproject.setImage(null);
            subprojectRepository.saveAndFlush(subproject);
        } catch (Exception e) {
            log.error(
                "Error deleting subproject image, subprojectId: {}",
                subprojectId
            );
            e.printStackTrace();
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
     *     <li>file uploder's user id</li>
     * </ul>
     * @param fileId {@link Long} fileid
     * @return  {@link Optional}<{@link FileDto}> file information, else empty of there's an error generating file data or not found
     */
    public Optional<FileDto> getFileInfo(Long fileId) {
        try {
            if (fileId == null) return Optional.empty();
            File file = fileRepository
                .findById(fileId)
                .orElseThrow(ResourceNotFoundException::new);

            String uploaderId = null;
            if (Objects.nonNull(file.getProjectDetail())) {
                ProjectFileDetail projectDetail = file.getProjectDetail();
                User uploader = projectDetail.getUploader();
                uploaderId =
                    Objects.nonNull(uploader)
                        ? uploader.getId().toString()
                        : null;
            }
            if (Objects.nonNull(file.getSubprojectDetail())) {
                SubprojectFileDetail subprojectDetail =
                    file.getSubprojectDetail();
                User uploader = subprojectDetail.getUploader();
                uploaderId =
                    Objects.nonNull(uploader)
                        ? uploader.getId().toString()
                        : null;
            }

            return Optional.of(
                new FileDto()
                    .setId(file.getId().toString())
                    .setName(file.getName())
                    .setContentType(file.getContentType())
                    .setUrl(GET_FILE_URL + file.getId().toString())
                    .setSize(Long.valueOf(file.getBytes().length))
                    .setUploaderId(uploaderId)
            );
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error generating file info id: {}", fileId);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * find all files of a given project by id, (reports and attachment)
     *
     * @param projectId {@link Long} project id
     * @return {@link Optional}<{@link List}<{@link File}>>
     */
    @Transactional
    public Optional<List<File>> getProjectFiles(@NonNull Long projectId) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow();
            List<File> projectFiles = fileRepository.findAllByProject(
                project.getId()
            );
            return Optional.of(projectFiles);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
