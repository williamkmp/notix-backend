package com.william.notix.services;

import com.william.notix.dto.FindingLogDto;
import com.william.notix.entities.FindingLog;
import com.william.notix.repositories.FindingLogReposiory;
import com.william.notix.utils.values.TOPIC;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindingLogService {

    private final FindingLogReposiory logReposiory;
    private final SimpMessagingTemplate socket;

    @Transactional
    public void publishLog(@NonNull FindingLog newLog) {
        FindingLog log = logReposiory.save(newLog);
        Long findingId = newLog.getFinding().getId();
        socket.convertAndSend(
            TOPIC.findingLog(findingId),
            new FindingLogDto()
                .setId(log.getId().toString())
                .setTitle(log.getTitle())
                .setMessage(log.getMessage())
                .setActorId(log.getActor().getId().toString())
                .setFindingId(log.getFinding().getId().toString())
                .setCreatedAt(log.getCreatedAt())
        );
    }

    @Transactional
    public FindingLog createDetailChangeLog(
        @NonNull String attributeName,
        @NonNull String newValue
    ) {
        return new FindingLog()
            .setTitle("Updated Property")
            .setMessage(
                String.format(
                    """
                        <p><strong>{{actor.fullName}}</strong> set project's %s to</p>
                        <p><em><mark class="bg-sky-100 rounded-none px-0.5">"%s"</mark></em></p>
                    """,
                    attributeName,
                    newValue
                )
            );
    }
}
