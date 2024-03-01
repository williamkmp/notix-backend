package com.william.notix.services;

import com.william.notix.dto.InviteDto;
import com.william.notix.entities.Authority;
import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.ROLE;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AuthorityRepository authorityRepository;

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
     *
     * @param projectId
     * @param invite
     * @return
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

            return Optional.of(newMember);
        } catch (UserNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
