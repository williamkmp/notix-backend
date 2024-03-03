package com.william.notix.services;

import com.william.notix.entities.Authority;
import com.william.notix.entities.Project;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.utils.values.ROLE;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final ProjectRepository projectRepository;

    /**
     * get the user ROLE in a given project, else return empty if user have no access
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Project} project id
     * @return {@link Optional}<{@link ROLE}> user's role, else empty
     */
    public Optional<ROLE> getUserProjectRole(
        @NonNull Long userId,
        @NonNull Long projectId
    ) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);
            Long ownerId = project.getOwner().getId();
            if (Objects.equals(userId, ownerId)) {
                return Optional.of(ROLE.PROJECT_MANAGER);
            }
            Authority authority = authorityRepository
                .findByUserAndProject(userId, projectId)
                .orElseThrow(Exception::new);
            return Optional.ofNullable(authority.getRole());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
