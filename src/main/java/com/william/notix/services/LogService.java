package com.william.notix.services;

import com.william.notix.dto.ProjectLogDto;
import com.william.notix.entities.Project;
import com.william.notix.entities.ProjectLog;
import com.william.notix.entities.User;
import com.william.notix.entities.UserLog;
import com.william.notix.entities.UserlogDto;
import com.william.notix.repositories.ProjectLogRepository;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.UserLogRespository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.TOPIC;
import jakarta.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final UserLogRespository userLogRespository;
    private final ProjectLogRepository projectLogRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate socket;

    /**
     * logged user is invited to a project by another user
     *
     * @param inviterId {@link Long} user id, who sent the invite
     * @param inviteeId {@link Long} user id, who receives the invite
     * @param projectId {@link Long} project id
     */
    @Transactional
    public void logUserIsInvitedToProject(
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

            UserLog log = userLogRespository.saveAndFlush(
                new UserLog()
                    .setTitle("New Project")
                    .setMessage(
                        "<p><strong>{{ user.fullName }}</strong> added you to their project, <em>\"</em><strong><em><mark class=\"bg-sky-100 rounded-none px-0.5\">{{ project.name }}</mark></em></strong><em>\"</em></p>"
                    )
                    .setRefrencedProject(project)
                    .setRefrencedUser(inviter)
                    .setUpdatee(invitee)
            );

            socket.convertAndSend(
                TOPIC.userLogs(inviterId),
                new UserlogDto()
                    .setId(log.getId().toString())
                    .setMessage(log.getMessage())
                    .setUserId(log.getRefrencedUser().getId().toString())
                    .setProjectId(log.getRefrencedProject().getId().toString())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project's log that name is changed
     *
     * @param projectId {@link Long} updated project id
     * @param updaterId {@link Long} user who performs th update
     * @param oldTitle {@link String} prev title
     * @param newTitle {@link String} new title
     */
    @Transactional
    public void logProjectCreated(
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

            ProjectLog log = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Created")
                    .setMessage(
                        "<p><strong>{{ user.fullName }}</strong> created project.</p>"
                    )
                    .setRefrencedUser(updater)
                    .setUpdatee(project)
            );

            socket.convertAndSend(
                TOPIC.projectLogs(projectId),
                new ProjectLogDto()
                    .setId(log.getId().toString())
                    .setMessage(log.getMessage())
                    .setUserId(log.getRefrencedUser().getId().toString())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project's log that name is changed
     *
     * @param projectId {@link Long} updated project id
     * @param updaterId {@link Long} user who performs th update
     * @param oldTitle {@link String} prev title
     * @param newTitle {@link String} new title
     */
    @Transactional
    public void logProjectChangeName(
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

            ProjectLog log = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Update Name")
                    .setMessage(
                        "<p><strong>{{ user.fullName }}</strong> changed project's name. <br><em>\"</em><strong><em><mark class=\"bg-sky-100 rounded-none px-0.5\">"+oldTitle+"</mark></em></strong><em>\" </em><strong>→ </strong><em>\"</em><strong><em><mark class=\"bg-sky-100 rounded-none px-0.5\">"+newTitle+"</mark></em></strong><em>\".</em></p>"
                    )
                    .setRefrencedUser(updater)
                    .setUpdatee(project)
            );

            socket.convertAndSend(
                TOPIC.projectLogs(projectId),
                new ProjectLogDto()
                    .setId(log.getId().toString())
                    .setMessage(log.getMessage())
                    .setUserId(log.getRefrencedUser().getId().toString())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project log for ownership transfer
     *
     * @param projectId {@link Long} updated project id
     * @param newOwnerId {@link Long} new owner's user id
     */
    @Transactional
    public void logProjectOwnershipTransfer(
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

            ProjectLog log = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Transfered Ownership")
                    .setMessage(
                        "<p>Project ownership transfered to <br>\"<strong><em>{{ user.fullName }}</em></strong>\"<br><em>{{ user.email }}</em></p>"
                    )
                    .setRefrencedUser(newOwner)
                    .setUpdatee(project)
            );

            socket.convertAndSend(
                TOPIC.projectLogs(projectId),
                new ProjectLogDto()
                    .setId(log.getId().toString())
                    .setMessage(log.getMessage())
                    .setUserId(log.getRefrencedUser().getId().toString())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add to project log for active period change
     *
     * @param projectId {@link Long} updated project id
     * @param updaterId {@link Long} user id who performs update
     * @param newStartDate {@link Date} new start date
     * @param newEndDate {@link Date} new end date
     */
    @Transactional
    public void logProjectUpdatePeriod(
        @NonNull Long projectId,
        @NonNull Long updaterId,
        @NonNull Date newStartDate,
        @NonNull Date newEndDate
    ) {
        try {
            SimpleDateFormat dtmFormatter = new SimpleDateFormat(
                "MMMM d, yyyy"
            );
            User updater = userRepository
                .findById(updaterId)
                .orElseThrow(Exception::new);
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(Exception::new);

            ProjectLog log = projectLogRepository.saveAndFlush(
                new ProjectLog()
                    .setTitle("Update Period")
                    .setMessage(
                        "<p><strong>{{ user.fullName }} </strong>updated project's active period to</p><p><em><mark class=\"bg-sky-100 rounded-none px-0.5\">"+dtmFormatter.format(newStartDate)+" → "+dtmFormatter.format(newEndDate)+"</mark></em></p>"
                    )
                    .setRefrencedUser(updater)
                    .setUpdatee(project)
            );

            socket.convertAndSend(
                TOPIC.projectLogs(projectId),
                new ProjectLogDto()
                    .setId(log.getId().toString())
                    .setMessage(log.getMessage())
                    .setUserId(log.getRefrencedUser().getId().toString())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
