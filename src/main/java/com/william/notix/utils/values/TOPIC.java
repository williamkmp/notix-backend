package com.william.notix.utils.values;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TOPIC {

    /**
     * STOMP destination url for /topic/user/{userId}/project/previews
     *
     * @param userId {@link Long} user id
     * @return {@link String} STOMP destination path
     */
    public static String userProjectPreviews(Long userId) {
        return "/topic/user/" + userId.toString() + "/project/previews";
    }

    /**
     * STOMP destination url for /topic/user/{userId}/project/{projectId}/errors
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @return {@link String} STOMP destination path
     */
    public static String userProjectErrors(Long userId, Long projectId) {
        return (
            "/topic/user/" +
            userId.toString() +
            "/project/" +
            projectId.toString() +
            "/errors"
        );
    }

    /**
     * STOMP destination url for /topic/user/{userId}/logs
     *
     * @param userId {@link Long} user id
     * @return {@link String} STOMP destination path
     */
    public static String userLogs(Long userId) {
        return "/topic/user/" + userId.toString() + "/logs";
    }
}
