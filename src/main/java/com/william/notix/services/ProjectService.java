package com.william.notix.services;

import com.william.notix.dto.InviteDto;
import com.william.notix.dto.ProjectDto;
import com.william.notix.dto.ProjectPreviewDto;
import com.william.notix.entities.Authority;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.PREVIEW_ACTION;
import com.william.notix.utils.values.ROLE;
import com.william.notix.utils.values.TOPIC;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final LogService logService;
    private final FileService fileService;
    private final DateTimeService dtmService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AuthorityRepository authorityRepository;
    private final SimpMessagingTemplate socket;

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

            authorityRepository.save(
                new Authority()
                    .setProject(project)
                    .setUser(newMember)
                    .setRole(memberRole)
            );

            socket.convertAndSend(
                TOPIC.userProjectPreviews(newMember.getId()),
                new ProjectPreviewDto()
                    .setAction(PREVIEW_ACTION.ADD)
                    .setId(project.getId().toString())
                    .setName(project.getName())
                    .setImageId(
                        project.getImage() != null
                            ? project.getImage().getId().toString()
                            : null
                    )
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
     * update a given project data.
     *
     * @param projectId {@link Long} target project id
     * @param updaterId {@link Long} target project id
     * @param newProjectData {@link ProjectDto} new data
     * @return {@link Optional}<{@link Project}> updated project data, else empty if update failed
     */
    @Transactional
    public Optional<Project> updateProject(
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
            LocalDate oldStartDate = dtmService.toLocalDate(
                project.getStartDate()
            );
            LocalDate oldEndDate = dtmService.toLocalDate(project.getEndDate());
            LocalDate newStarDate = dtmService.toLocalDate(
                newProjectData.getStartDate()
            );
            LocalDate newEndDate = dtmService.toLocalDate(
                newProjectData.getEndDate()
            );

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
                (!Objects.equals(oldStartDate, newStarDate) ||
                    !Objects.equals(oldEndDate, newEndDate));

            if (isImageChange && newImageId == null) {
                fileService.deleteImageOfProject(projectId);
            }

            if (isImageChange && newImageId != null) {
                fileService.updateProjectImage(projectId, newImageId);
            }

            project.setName(newProjectData.getName());
            project.setStartDate(newProjectData.getStartDate());
            project.setEndDate(newProjectData.getEndDate());
            project.setOwner(newOwner);
            project = projectRepository.save(project);

            socket.convertAndSend(
                TOPIC.projectPreview(projectId),
                new ProjectPreviewDto()
                    .setImageId(newImageId != null ? newImageId.toString() : null)
                    .setId(projectId.toString())
                    .setName(project.getName())
            );

            if (isTitleUpdated) {
                logService.logProjectChangeName(
                    projectId,
                    updaterId,
                    oldProjectName,
                    newProjectData.getName()
                );
            }

            if (isDurationChanged) {
                logService.logProjectUpdatePeriod(
                    projectId,
                    newOwner.getId(),
                    newProjectData.getStartDate(),
                    newProjectData.getEndDate()
                );
            }

            if (isOwnerChanged) {
                logService.logProjectOwnershipTransfer(
                    projectId,
                    newOwner.getId()
                );
            }
            return Optional.of(project);
        } catch (UserNotFoundException | ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
