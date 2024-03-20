package com.william.notix.services;

import com.william.notix.entities.Authority;
import com.william.notix.entities.Project;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.utils.values.ROLE;
import jakarta.transaction.Transactional;
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
     * @param projectId {@link Long} project id
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

    /**
     * update a member role in a given project
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @param newRole {@link ROLE} new role
     * @return {@link Optional}<{@link ROLE}> new role, else empty if fails
     */
    @Transactional
    public Optional<ROLE> updateMemberRole(
        @NonNull Long userId,
        @NonNull Long projectId,
        @NonNull ROLE newRole
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
            authority.setRole(newRole);
            authority = authorityRepository.save(authority);
            return Optional.of(authority.getRole());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * check if a given role can do operation to a project member's
     * i.e. ADD, DELETE, UPDATE.
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can operate project member
     */
    public boolean roleCanOperateMember(@NonNull ROLE role) {
        return Objects.equals(role, ROLE.PROJECT_MANAGER);
    }

    /**
     * check if a given role can do operation to a project data
     * , UPDATE project data i.e. name, active-period, etc.
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can operate project member
     */
    public boolean roleCanOperateProject(@NonNull ROLE role) {
        return Objects.equals(role, ROLE.PROJECT_MANAGER);
    }
}
