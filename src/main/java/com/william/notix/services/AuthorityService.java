package com.william.notix.services;

import com.william.notix.entities.Authority;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectFileDetail;
import com.william.notix.entities.Subproject;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.utils.values.FILE_TYPE;
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
    private final SubprojectRepository subprojectRepository;
    private final ProjectRepository projectRepository;
    private final FileService fileService;

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
     * get the user ROLE in a given subproject, else return empty if user have no access
     *
     * @param userId {@link Long} user id
     * @param subprojectId {@link Long} project id
     * @return {@link Optional}<{@link ROLE}> user's role, else empty
     */
    @Transactional
    public Optional<ROLE> getUserSubprojectRole(
        @NonNull Long userId,
        @NonNull Long subprojectId
    ) {
        try {
            Optional<Authority> authority =
                authorityRepository.findByUserAndSubproject(
                    userId,
                    subprojectId
                );
            if (authority.isPresent()) {
                return Optional.of(authority.get().getRole());
            }

            Subproject subproject = subprojectRepository
                .findById(subprojectId)
                .orElseThrow(ResourceNotFoundException::new);
            Project parentProject = subproject.getProject();
            Long ownerId = parentProject.getOwner().getId();
            boolean isUserOwner = Objects.equals(ownerId, userId);
            if (isUserOwner) {
                return Optional.of(ROLE.PROJECT_MANAGER);
            }

            return Optional.empty();
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

    /**
     * check if a given role can add subprojects
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can operate project member
     */
    public boolean roleCanAddSubproject(@NonNull ROLE role) {
        return Objects.equals(role, ROLE.PROJECT_MANAGER);
    }

    /**
     * check if a given role can upload attachment file
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can upload attachment file
     */
    public boolean roleCanUploadAttcahment(@NonNull ROLE role) {
        return (
            Objects.equals(role, ROLE.DEVELOPER) ||
            Objects.equals(role, ROLE.PROJECT_MANAGER)
        );
    }

    /**
     * check if a given role can upload report file
     *
     * @param role {@link ROLE}
     * @return {@link boolean} role can upload report file
     */
    public boolean roleCanUploadReport(@NonNull ROLE role) {
        return (
            Objects.equals(role, ROLE.TECHNICAL_WRITER) ||
            Objects.equals(role, ROLE.PROJECT_MANAGER)
        );
    }

    /**
     * check if user can delete a preject file,
     * return if user can delete a certain project file,
     * else return Optioan.empty() if user cannot access project
     *
     * @param userId {@link Long} user id
     * @param fileId {@link Long} file id
     * @return {@link Optional}<{@link Boolean}>
     */
    public Optional<Boolean> userCanDeleteProjectFile(
        @NonNull Long userId,
        @NonNull Long fileId
    ) {
        try {
            File file = fileService.findById(fileId).orElseThrow();

            ProjectFileDetail fileDetail = Optional
                .of(file.getProjectDetail())
                .orElseThrow();

            Project project = fileDetail.getProject();
            ROLE userRole =
                this.getUserProjectRole(userId, project.getId()).orElseThrow();

            FILE_TYPE fileType = fileDetail.getFileType();
            Long uploaderId = fileDetail.getUploader().getId();
            boolean userIsUploader = Objects.equals(uploaderId, userId);

            Boolean canDelete = Boolean.FALSE;
            if (userRole == ROLE.PROJECT_MANAGER) {
                canDelete = Boolean.TRUE;
            } else if (fileType == FILE_TYPE.ATTACHMENT) {
                boolean roleMatched = roleCanUploadAttcahment(userRole);
                canDelete = Boolean.valueOf(roleMatched && userIsUploader);
            } else if (fileType == FILE_TYPE.REPORT) {
                boolean roleMatched = roleCanUploadReport(userRole);
                canDelete = Boolean.valueOf(roleMatched && userIsUploader);
            }

            return Optional.of(canDelete);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
