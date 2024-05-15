package com.william.notix.services;

import com.william.notix.dto.InviteDto;
import com.william.notix.dto.ProjectDto;
import com.william.notix.entities.Authority;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectFileDetail;
import com.william.notix.entities.ProjectLog;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.repositories.FileRepository;
import com.william.notix.repositories.ProjectFileRepository;
import com.william.notix.repositories.ProjectLogRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.FILE_TYPE;
import com.william.notix.utils.values.ROLE;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final LogService logService;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final DateTimeService dtmService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectLogRepository projectLogRepository;
    private final AuthorityRepository authorityRepository;
    private final ProjectFileRepository projectFileRepository;

    /**
     * insert a new project
     *
     * @param newProject {@link Project} new project data
     * @param ownerId {@link Long} user id
     * @return {@link Optional}<{@link Project}> saved project data
     * @throws UserNotFoundException when caller not found
     */
    @Transactional
    public Optional<Project> createProject(
        @NonNull Project newProject,
        @NonNull Long ownerId
    ) throws UserNotFoundException {
        try {
            User owner = userRepository
                .findById(ownerId)
                .orElseThrow(UserNotFoundException::new);
            newProject.setOwner(owner);
            Project savedProject = projectRepository.save(newProject);
            logService.createProject(savedProject.getId(), owner.getId());
            return Optional.of(savedProject);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * get all project affiliated with a certain user by user's id
     *
     * @param userId {@link Long} user id
     * @return {@link List}<{@link Project}> user's affiliated projects
     * @throws UserNotFoundException when user not found
     */
    public List<Project> findAllByUser(@NonNull Long userId)
        throws UserNotFoundException {
        User user = userRepository
            .findById(userId)
            .orElseThrow(UserNotFoundException::new);
        return projectRepository.findAllByUser(user.getId());
    }

    /**
     * get a project by a given id
     *
     * @param projectId {@link Long} project id
     * @return {@link Optional}<{@link Project}>
     */
    public Optional<Project> findById(@NonNull Long projectId) {
        return projectRepository.findById(projectId);
    }

    /**
     * add an existing user to a project as a member, and notifying invited user
     *
     * @param projectId {@link Long} project id
     * @param invite {@link InviteDto} invite data
     * @return {@link Optional}<{@link User}> invited user, else empty if add operation failed
     */
    @Transactional
    public Optional<User> addMember(
        @NonNull Long projectId,
        @NonNull InviteDto invite
    ) {
        try {
            User newMember = userRepository
                .findByEmail(invite.getEmail())
                .orElseThrow(UserNotFoundException::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            ROLE memberRole = Optional
                .ofNullable(invite.getRole())
                .orElse(ROLE.VIEWER);

            Long ownerId = project.getOwner().getId();
            Long memberId = newMember.getId();

            if (Objects.equals(ownerId, memberId)) {
                return Optional.of(project.getOwner());
            }

            Optional<Authority> authority =
                authorityRepository.findByUserAndProject(memberId, projectId);

            if (authority.isPresent()) {
                return Optional.empty();
            }

            authorityRepository.save(
                new Authority()
                    .setProject(project)
                    .setUser(newMember)
                    .setRole(memberRole)
            );

            return Optional.of(newMember);
        } catch (UserNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * update a given project header data, this includes
     * <ul>
     *      <li>project name</li>
     *      <li>project end date</li>
     *      <li>project start date</li>
     *      <li>project end date</li>
     * </ul>
     *
     * @param projectId {@link Long} target project id
     * @param updaterId {@link Long} target project id
     * @param newProjectData {@link ProjectDto} new data
     * @return {@link Optional}<{@link Project}> updated project data, else empty if update failed
     */
    @Transactional
    public Optional<Project> updateProjectHeader(
        Long projectId,
        Long updaterId,
        ProjectDto newProjectData
    ) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            User newOwner = userRepository
                .findById(Long.valueOf(newProjectData.getOwnerId()))
                .orElseThrow(UserNotFoundException::new);

            Long oldImageId = project.getImage() != null
                ? project.getImage().getId()
                : null;
            Long newImageId = newProjectData.getImageId() != null
                ? Long.valueOf(newProjectData.getImageId())
                : null;
            String oldProjectName = project.getName();
            Date oldStartDate = project.getStartDate();
            Date oldEndDate = project.getEndDate();
            Date newStarDate = newProjectData.getStartDate();
            Date newEndDate = newProjectData.getEndDate();

            boolean isImageChange = !Objects.equals(oldImageId, newImageId);
            boolean isTitleUpdated = !Objects.equals(
                project.getName(),
                newProjectData.getName()
            );
            boolean isOwnerChanged = !Objects.equals(
                project.getOwner().getId().toString(),
                newProjectData.getOwnerId()
            );
            boolean isDurationChanged =
                !dtmService.isSameDate(newStarDate, oldStartDate) ||
                !dtmService.isSameDate(newEndDate, oldEndDate);

            if (isImageChange && newImageId == null) {
                fileService.deleteImageOfProject(projectId);
            }

            if (isImageChange && newImageId != null) {
                fileService.updateProjectImage(projectId, newImageId);
            }

            project.setName(newProjectData.getName());
            project.setOwner(newOwner);
            project.setStartDate(newProjectData.getStartDate());
            project.setEndDate(newProjectData.getEndDate());
            project = projectRepository.save(project);

            if (isTitleUpdated) {
                logService.updateProjectName(
                    projectId,
                    updaterId,
                    oldProjectName,
                    newProjectData.getName()
                );
            }

            if (isDurationChanged) {
                logService.updateProjectPeriod(
                    projectId,
                    newOwner.getId(),
                    newProjectData.getStartDate(),
                    newProjectData.getEndDate()
                );
            }

            if (isOwnerChanged) {
                logService.projectTransferOwner(projectId, newOwner.getId());
            }
            return Optional.of(project);
        } catch (UserNotFoundException | ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * delete a project member
     *
     * @param projectId {@link Long} project id
     * @param memberId {@link Long} user id
     * @return {@link Optional}<{@link }>
     */
    public Optional<User> deletePageMember(
        @NonNull Long projectId,
        @NonNull Long memberId
    ) {
        try {
            Optional<Authority> maybeAUthority =
                authorityRepository.findByUserAndProject(memberId, projectId);

            if (maybeAUthority.isEmpty()) {
                throw new ResourceNotFoundException();
            }

            Authority authority = maybeAUthority.get();
            User member = userRepository
                .findById(memberId)
                .orElseThrow(UserNotFoundException::new);
            authorityRepository.delete(authority);

            return Optional.of(member);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * get project logs by a given project id, with paging
     *
     * @param projectId {@link Long} project id
     * @param page {@link Pageable} paging information
     * @return {@link Optional}<{@link {@link List}<{@link ProjectLog}>}>
     */
    public Optional<List<ProjectLog>> findLogsById(
        @NonNull Long projectId,
        @NonNull Pageable page
    ) {
        try {
            List<ProjectLog> updateRecords =
                projectLogRepository.findAllByProject(projectId, page);
            return Optional.of(updateRecords);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * add file as project attachment
     *
     * @param projectId {@link Long} project id
     * @param uploaderId {@link Long} user id
     * @param fileId {@link Long} file id
     * @return {@link Optional}<{@link ProjectFileDetail}>
     */
    @Transactional
    public Optional<ProjectFileDetail> addAttachmentToProject(
        @NonNull Long projectId,
        @NonNull Long uploaderId,
        @NonNull Long fileId
    ) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            File file = fileRepository
                .findById(fileId)
                .orElseThrow(Exception::new);

            User uploader = userRepository
                .findById(uploaderId)
                .orElseThrow(Exception::new);

            ProjectFileDetail fileDetail = projectFileRepository.save(
                new ProjectFileDetail()
                    .setFile(file)
                    .setFileType(FILE_TYPE.ATTACHMENT)
                    .setProject(project)
                    .setUploader(uploader)
            );

            return Optional.of(fileDetail);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * add file as project attachment
     *
     * @param projectId {@link Long} project id
     * @param uploaderId {@link Long} user id
     * @param fileId {@link Long} file id
     * @return {@link Optional}<{@link ProjectFileDetail}>
     */
    @Transactional
    public Optional<ProjectFileDetail> addReportToProject(
        @NonNull Long projectId,
        @NonNull Long uploaderId,
        @NonNull Long fileId
    ) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            File file = fileRepository
                .findById(fileId)
                .orElseThrow(Exception::new);

            User uploader = userRepository
                .findById(uploaderId)
                .orElseThrow(Exception::new);

            ProjectFileDetail fileDetail = projectFileRepository.save(
                new ProjectFileDetail()
                    .setFile(file)
                    .setFileType(FILE_TYPE.REPORT)
                    .setProject(project)
                    .setUploader(uploader)
            );

            return Optional.of(fileDetail);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
