package com.william.notix.services;

import com.william.notix.dto.LogDto;
import com.william.notix.entities.File;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectFileDetail;
import com.william.notix.entities.ProjectLog;
import com.william.notix.entities.User;
import com.william.notix.entities.UserLog;
import com.william.notix.repositories.ProjectFileRepository;
import com.william.notix.repositories.ProjectLogRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.UserLogRespository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.PROJECT_ROLE;
import com.william.notix.utils.values.TOPIC;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final UserLogRespository userLogRespository;
    private final ProjectLogRepository projectLogRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectFileRepository projectFileRepository;
    private final SimpMessagingTemplate socket;

    /**
     * Serialize a given User updateRecord record
     *
     * @param updateRecord {@link UserLog} updateRecord
     * @return {@link Optional}<{@link LogDto}> updateRecord dto
     */
    @Transactional
    public Optional<LogDto> serializeUserLog(@NonNull UserLog updateRecord) {
        try {
            String userId = updateRecord.getRefrencedUser() != null
                ? updateRecord.getRefrencedUser().getId().toString()
                : null;
            String projectId = updateRecord.getRefrencedProject() != null
                ? updateRecord.getRefrencedProject().getId().toString()
                : null;
            String subprojectId = updateRecord.getRefrencedSubproject() != null
                ? updateRecord.getRefrencedSubproject().getId().toString()
                : null;
            return Optional.of(
                new LogDto()
                    .setId(updateRecord.getId().toString())
                    .setTitle(updateRecord.getTitle())
                    .setProjectId(projectId)
                    .setUserId(userId)
                    .setSubprojectId(subprojectId)
                    .setMessage(updateRecord.getMessage())
                    .setCreatedAt(updateRecord.getCreatedAt())
            );
        } catch (Exception e) {
            log.error("Error serializing UserLog id:{}", updateRecord.getId());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Serialize a given User updateRecord record
     *
     * @param updateRecord {@link ProjectLog} updateRecord
     * @return {@link Optional}<{@link LogDto}> updateRecord dto
     */
    @Transactional
    public Optional<LogDto> serializeProjectLog(
        @NonNull ProjectLog updateRecord
    ) {
        try {
            String userId = updateRecord.getRefrencedUser() != null
                ? updateRecord.getRefrencedUser().getId().toString()
                : null;
            String projectId = updateRecord.getUpdatee().getId().toString();
            String subprojectId = updateRecord.getRefrencedSubproject() != null
                ? updateRecord.getRefrencedSubproject().getId().toString()
                : null;
            return Optional.of(
                new LogDto()
                    .setId(updateRecord.getId().toString())
                    .setTitle(updateRecord.getTitle())
                    .setUserId(userId)
                    .setProjectId(projectId)
                    .setSubprojectId(subprojectId)
                    .setMessage(updateRecord.getMessage())
                    .setCreatedAt(updateRecord.getCreatedAt())
            );
        } catch (Exception e) {
            log.error(
                "Error serializing ProjectLog id:{}",
                updateRecord.getId()
            );
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * logged user is invited to a project by another user
     *
     * @param inviterId {@link Long} user id, who sent the invite
     * @param inviteeId {@link Long} user id, who receives the invite
     * @param projectId {@link Long} project id
     */
    @Transactional
    public void userInvitedToProject(
        @NonNull Long inviterId,
        @NonNull Long inviteeId,
        @NonNull Long projectId
    ) {
        try {
            User inviter = userRepository
                .findById(inviterId)
                .orElseThrow(Exception::new);
            User invitee = userRepository
                .findById(inviteeId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            UserLog updateRecord = userLogRespository.saveAndFlush(
                new UserLog()
                    .setTitle("New Project")
                    .setMessage(
                        "<p><strong>{{user.fullName}}</strong> added you to their project, <em>\"</em><strong><em><mark class=\"bg-sky-100 rounded-none px-0.5\">{{project.name}}</mark></em></strong><em>\"</em></p>"
                    )
                    .setRefrencedProject(project)
                    .setRefrencedUser(inviter)
                    .setUpdatee(invitee)
            );

            LogDto dto = serializeUserLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.userLogs(inviterId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project's updateRecord that name is changed
     *
     * @param projectId {@link Long} updated project id
     * @param updaterId {@link Long} user who performs th update
     * @param oldTitle {@link String} prev title
     * @param newTitle {@link String} new title
     */
    @Transactional
    public void createProject(
        @NonNull Long projectId,
        @NonNull Long creatorId
    ) {
        try {
            User updater = userRepository
                .findById(creatorId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Created")
                    .setMessage(
                        "<p><strong>{{user.fullName}}</strong> created project.</p>"
                    )
                    .setRefrencedUser(updater)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project's updateRecord that name is changed
     *
     * @param projectId {@link Long} updated project id
     * @param updaterId {@link Long} user who performs th update
     * @param oldTitle {@link String} prev title
     * @param newTitle {@link String} new title
     */
    @Transactional
    public void updateProjectName(
        @NonNull Long projectId,
        @NonNull Long updaterId,
        @NonNull String oldTitle,
        @NonNull String newTitle
    ) {
        try {
            User updater = userRepository
                .findById(updaterId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Update Name")
                    .setMessage(
                        "<p><strong>{{user.fullName}}</strong> changed project's name. <br><em>\"</em><strong><em><mark class=\"bg-sky-100 rounded-none px-0.5\">" +
                        oldTitle +
                        "</mark></em></strong><em>\" </em><strong>→ </strong><em>\"</em><strong><em><mark class=\"bg-sky-100 rounded-none px-0.5\">" +
                        newTitle +
                        "</mark></em></strong><em>\".</em></p>"
                    )
                    .setRefrencedUser(updater)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project updateRecord for ownership transfer
     *
     * @param projectId {@link Long} updated project id
     * @param newOwnerId {@link Long} new owner's user id
     */
    @Transactional
    public void projectTransferOwner(
        @NonNull Long projectId,
        @NonNull Long newOwnerId
    ) {
        try {
            User newOwner = userRepository
                .findById(newOwnerId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Transfered Ownership")
                    .setMessage(
                        "<p>Project ownership transfered to <br>\"<strong><em>{{user.fullName}}</em></strong>\"<br><em>{{user.email}}</em></p>"
                    )
                    .setRefrencedUser(newOwner)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project updateRecord for active period change
     *
     * @param projectId {@link Long} updated project id
     * @param updaterId {@link Long} user id who performs update
     * @param newStartDate {@link Date} new start date
     * @param newEndDate {@link Date} new end date
     */
    @Transactional
    public void updateProjectPeriod(
        @NonNull Long projectId,
        @NonNull Long updaterId,
        @NonNull Date newStartDate,
        @NonNull Date newEndDate
    ) {
        try {
            User updater = userRepository
                .findById(updaterId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Update Period")
                    .setMessage(
                        "<p><strong>{{user.fullName}} </strong>updated project's active period to</p><p><em><mark class=\"bg-sky-100 rounded-none px-0.5\">{{project.startDate}} → {{project.endDate}}</mark></em></p>"
                    )
                    .setRefrencedUser(updater)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project updateRecord a new member is added
     *
     * @param projectId {@link Long} project id
     * @param newMemberId {@link Long} user id
     */
    @Transactional
    public void projcetMemberAdded(
        @NonNull Long projectId,
        @NonNull Long newMemberId
    ) {
        try {
            User member = userRepository
                .findById(newMemberId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("New Member")
                    .setMessage(
                        "<p><strong>{{user.fullName}}</strong> is added to this project.</p>"
                    )
                    .setRefrencedUser(member)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log member role is changed in a certaion project
     *
     * @param projectId {@link Long} project id
     * @param memberId {@link Long} user id
     * @param role {@link PROJECT_ROLE} new role
     */
    @Transactional
    public void projectMemberRoleChange(
        @NonNull Long projectId,
        @NonNull Long memberId,
        @NonNull PROJECT_ROLE role
    ) {
        try {
            User member = userRepository
                .findById(memberId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            String roleDisplayName =
                switch (role) {
                    case MEMBER -> "Member";
                    case DEVELOPER -> "Developer";
                    case TECHNICAL_WRITER -> "Technical Writer";
                    default -> throw new IllegalArgumentException(
                        "Unexpected value: " + role
                    );
                };

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Role Changed")
                    .setMessage(
                        "<p><strong>{{user.fullName}}'s</strong> role is changed to <em><mark class=\"bg-sky-100 rounded-none px-0.5\">" +
                        roleDisplayName +
                        "</mark></em></p>"
                    )
                    .setRefrencedUser(member)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void projectMemberDelete(
        @NonNull Long projectId,
        @NonNull Long memberId
    ) {
        try {
            User member = userRepository
                .findById(memberId)
                .orElseThrow(Exception::new);

            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Member Removed")
                    .setMessage(
                        "<p><strong>{{user.fullName}}</strong> is removed from this project</p>"
                    )
                    .setRefrencedUser(member)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(projectId), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log project added new attachment/report
     *
     * @param fileDetailId {@link Long} ProjectFileDetail id
     */
    @Transactional
    public void projectNewFile(@NonNull Long fileDetailId) {
        try {
            ProjectFileDetail fileDetail = projectFileRepository
                .findById(fileDetailId)
                .orElseThrow(Exception::new);

            User uploader = Objects.requireNonNull(fileDetail.getUploader());
            Project project = Objects.requireNonNull(fileDetail.getProject());
            File file = Objects.requireNonNull(fileDetail.getFile());

            String logTitle =
                switch (fileDetail.getFileType()) {
                    case ATTACHMENT -> "New Attachment";
                    case REPORT -> "New Report";
                    case PUBLIC -> "New File";
                };

            ProjectLog updateRecord = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle(logTitle)
                    .setMessage(
                        "<p><strong>{{user.fullName}}'s</strong> uploaded a new file <em><mark class=\"bg-sky-100 rounded-none px-0.5\">" +
                        file.getName() +
                        "</mark></em></p>"
                    )
                    .setRefrencedUser(uploader)
                    .setUpdatee(project)
            );

            LogDto dto = serializeProjectLog(updateRecord)
                .orElseThrow(Exception::new);

            socket.convertAndSend(TOPIC.projectLogs(project.getId()), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
