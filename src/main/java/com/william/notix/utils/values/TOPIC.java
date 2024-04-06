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

    /**
     * STOMP destination url for /topic/project/{projectId}
     *
     * @param projectId {@link Long} project id
     * @return {@link String} STOMP destination path
     */
    public static String project(Long projectId) {
        return "/topic/project/" + projectId.toString();
    }

    /**
     * STOMP destination url for /topic/project/{projectId}/logs
     *
     * @param projectId {@link Long} project id
     * @return {@link String} STOMP destination path
     */
    public static String projectLogs(Long projectId) {
        return "/topic/project/" + projectId.toString() + "/logs";
    }

    /**
     * STOMP destination url for /topic/project/{projectId}/members
     *
     * @param projectId {@link Long} project id
     * @return {@link String} STOMP destination path
     */
    public static String projectMembers(Long projectId) {
        return "/topic/project/" + projectId.toString() + "/members";
    }

    /**
     * STOMP destination url for /topic/project/{projectId}/preview
     *
     * @param projectId {@link Long} project id
     * @return {@link String} STOMP destination path
     */
    public static String projectPreview(Long projectId) {
        return "/topic/project/" + projectId.toString() + "/preview";
    }

    /**
     * STOMP destination url for /topic/project/{projectId}/subprojects
     *
     * @param projectId {@link Long} project id
     * @return {@link String} STOMP destination path
     */
    public static String projectSubprojects(Long projectId) {
        return "/topic/project/" + projectId.toString() + "/subprojects";
    }
}
