package com.william.notix.services;

import com.william.notix.entities.Finding;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectAuthority;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.SubprojectAuthority;
import com.william.notix.entities.User;
import com.william.notix.repositories.ProjectAuthorityRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.SubprojectAuthorityRepository;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.utils.values.PROJECT_ROLE;
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

    private final ProjectAuthorityRepository projectAuthorityRepository;
    private final SubprojectAuthorityRepository subprojectAuthorityRepository;
    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;

    /**
     * get the user ROLE in a given project, else return empty if user have no access
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @return {@link Optional}<{@link PROJECT_ROLE}> user's role, else empty
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
            ProjectAuthority authority = projectAuthorityRepository
                .findByUserAndProject(userId, projectId)
                .orElseThrow(Exception::new);
            ROLE userRole = mapProjectRole(authority.getRole());
            return Optional.ofNullable(userRole);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * map project role to a given role
     *
     * @param projectRole
     * @return
     */
    public ROLE mapProjectRole(@NonNull PROJECT_ROLE projectRole) {
        return switch (projectRole) {
            case DEVELOPER -> ROLE.DEVELOPER;
            case MEMBER -> ROLE.MEMBER;
            case TECHNICAL_WRITER -> ROLE.TECHNICAL_WRITER;
            default -> throw new IllegalArgumentException(
                "Unexpected value: " + projectRole
            );
        };
    }

    /**
     * get the user ROLE in a given subproject, else return empty if user have no access
     *
     * @param userId {@link Long} user id
     * @param subprojectId {@link Long} project id
     * @return {@link Optional}<{@link PROJECT_ROLE}> user's role, else empty
     */
    @Transactional
    public Optional<ROLE> getUserSubprojectRole(
        @NonNull Long userId,
        @NonNull Long subprojectId
    ) {
        try {
            Subproject subproject = subprojectRepository
                .findById(subprojectId)
                .orElseThrow();

            Long ownerId = subproject.getProject().getOwner().getId();
            if (Objects.equals(ownerId, userId)) {
                return Optional.of(ROLE.PROJECT_MANAGER);
            }

            Long projectId = subproject.getProject().getId();
            ProjectAuthority projectAuthority = projectAuthorityRepository
                .findByUserAndProject(userId, projectId)
                .orElseThrow();

            Optional<SubprojectAuthority> subprojectAuthority =
                subprojectAuthorityRepository.findByUserAndProject(
                    userId,
                    projectId
                );

            PROJECT_ROLE projecRole = projectAuthority.getRole();

            if (
                Objects.equals(projecRole, PROJECT_ROLE.MEMBER) &&
                subprojectAuthority.isPresent()
            ) {
                return Optional.of(ROLE.CONSULTANT);
            }

            ROLE userRole = mapProjectRole(projecRole);
            return Optional.of(userRole);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<ROLE> getRoleOfFinding(
        @NonNull User user,
        @NonNull Finding finding
    ) {
        Subproject subproject = finding.getSubproject();
        return getUserSubprojectRole(user.getId(), subproject.getId());
    }

    /**
     * update a member role in a given project
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @param newRole {@link PROJECT_ROLE} new role
     * @return {@link Optional}<{@link PROJECT_ROLE}> new role, else empty if fails
     */
    @Transactional
    public Optional<PROJECT_ROLE> updateMemberRole(
        @NonNull Long userId,
        @NonNull Long projectId,
        @NonNull PROJECT_ROLE newRole
    ) {
        try {
            ProjectAuthority authority = projectAuthorityRepository
                .findByUserAndProject(userId, projectId)
                .orElseThrow(Exception::new);
            authority.setRole(newRole);
            authority = projectAuthorityRepository.save(authority);
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

    /**
     * check if a given role can add subprojects
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can operate project member
     */
    public boolean roleCanOperateSubproject(@NonNull ROLE role) {
        return Objects.equals(role, ROLE.PROJECT_MANAGER);
    }

    /**
     * check if a given role can upload attachment file
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can upload attachment file
     */
    public boolean roleCanOperateAttachment(@NonNull ROLE role) {
        return Objects.equals(role, ROLE.DEVELOPER);
    }

    /**
     * check if a given role can upload report file
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can upload report file
     */
    public boolean roleCanOperateReport(@NonNull ROLE role) {
        return Objects.equals(role, ROLE.TECHNICAL_WRITER);
    }
}
