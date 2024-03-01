package com.william.notix.services;

import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.entities.UserLog;
import com.william.notix.entities.UserlogDto;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.UserLogRespository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.TOPIC;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final UserLogRespository userLogRespository;
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

            UserLog log = userLogRespository.save(
                new UserLog()
                    .setMessage(
                        "{{ user.fullName }} added you to project, \"{{ project.name }}\""
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
            // Ignore error
        }
    }
}
